package io.github.wsyong11.gameforge.framework.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class ContextStackTest {
	@BeforeEach
	void resetGlobal() {
		Context.setGlobal(null);
		Context.forceUpdateGlobal();
	}

	@AfterEach
	void checkStack() {
		ContextStack stack = ContextStack.getInstance();
		assertEquals(0, stack.size());
	}

	// ====================================================
	// 基础 push / pop
	// ====================================================

	@Test
	void pushAndPop_shouldWork() {
		ContextStack stack = ContextStack.getInstance();
		TestContext ctx = new TestContext("A", false);

		stack.push(ctx);

		assertEquals(ctx, stack.getCurrent());
		assertEquals(1, stack.size());

		stack.pop(ctx);

		assertNull(stack.getCurrent());
		assertEquals(0, stack.size());
	}

	@Test
	void pop_shouldThrowIfEmpty() {
		ContextStack stack = ContextStack.getInstance();
		assertThrows(IllegalStateException.class,
			() -> stack.pop(new TestContext("B", false)));
	}

	@Test
	void pop_shouldThrowIfWrongContext() {
		ContextStack stack = ContextStack.getInstance();
		TestContext ctx1 = new TestContext("C", false);
		TestContext ctx2 = new TestContext("D", false);

		stack.push(ctx1);

		assertThrows(IllegalArgumentException.class,
			() -> stack.pop(ctx2));

		stack.pop(ctx1);
	}

	// ====================================================
	// DISABLE 分支
	// ====================================================

	@Test
	void pushDisable_shouldHideContext() {
		ContextStack stack = ContextStack.getInstance();
		TestContext ctx = new TestContext("E", false);

		stack.push(ctx);
		stack.pushDisable();

		assertNull(stack.getCurrent());

		stack.popDisable();
		assertEquals(ctx, stack.getCurrent());

		stack.pop(ctx);
	}

	@Test
	void disablePush_shouldRejectNonNullContext() {
		assertThrows(IllegalArgumentException.class,
			() -> new ContextStack.StackItem(
				new TestContext("F", false),
				ContextStack.StackItemType.DISABLE,
				Thread.currentThread(),
				false,
				false
			));
	}

	// ====================================================
	// HIDDEN 分支
	// ====================================================

	@Test
	void hidden_shouldCutUpperStackVisibility() {
		ContextStack stack = ContextStack.getInstance();

		TestContext a = new TestContext("G", false);
		TestContext b = new TestContext("H", false);

		stack.push(a);
		stack.pushHidden(null);
		stack.push(b);

		List<Context> snapshot = stack.getStackSnapshot().toList();

		// 顺序：b, hidden(null), global
		assertTrue(snapshot.contains(b));
		assertFalse(snapshot.contains(a));

		stack.pop(b);
		stack.popHidden(null);
		stack.pop(a);
	}

	// ====================================================
	// Debug 传播
	// ====================================================

	@Test
	void debug_shouldPropagateFromParent() {
		ContextStack stack = ContextStack.getInstance();

		TestContext parent = new TestContext("I", true);
		TestContext child = new TestContext("J", false);

		stack.push(parent);
		stack.push(child);

		assertTrue(stack.isDebug());

		stack.pop(child);
		stack.pop(parent);
	}

	@Test
	void debug_shouldRespectGlobal() {
		TestContext global = new TestContext("K", true);
		Context.setGlobal(global);
		Context.forceUpdateGlobal();

		ContextStack stack = ContextStack.getInstance();
		assertTrue(stack.isDebug());
	}

	// ====================================================
	// forceUpdateGlobal
	// ====================================================

	@Test
	void forceUpdateGlobal_shouldUpdateAllStacks() throws Exception {
		TestContext g1 = new TestContext("L", false);
		Context.setGlobal(g1);

		ContextStack mainStack = ContextStack.getInstance();

		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<ContextStack> future = executor.submit(ContextStack::getInstance);
		ContextStack otherStack = future.get();

		TestContext g2 = new TestContext("M", true);
		Context.setGlobal(g2);
		Context.forceUpdateGlobal();

		assertEquals(g2, mainStack.getCurrent());
		assertEquals(g2, otherStack.getCurrent());

		executor.shutdown();
	}

	// ====================================================
	// 线程安全检查
	// ====================================================

	@Test
	void shouldThrowWhenAccessFromOtherThread() throws Exception {
		ContextStack stack = ContextStack.getInstance();
		TestContext ctx = new TestContext("N", false);

		ExecutorService executor = Executors.newSingleThreadExecutor();

		Future<Exception> future = executor.submit(() -> {
			try {
				stack.push(ctx);
				return null;
			} catch (Exception e) {
				return e;
			}
		});

		Exception e = future.get();
		executor.shutdown();

		assertInstanceOf(IllegalCallerException.class, e);
	}

	// ====================================================
	// size
	// ====================================================

	@Test
	void size_shouldReflectStackDepth() {
		ContextStack stack = ContextStack.getInstance();

		TestContext a = new TestContext("O", false);
		TestContext b = new TestContext("P", false);

		stack.push(a);
		stack.push(b);

		assertEquals(2, stack.size());

		stack.pop(b);
		stack.pop(a);

		assertEquals(0, stack.size());
	}
}
