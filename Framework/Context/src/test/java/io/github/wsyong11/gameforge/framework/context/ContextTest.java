package io.github.wsyong11.gameforge.framework.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class ContextTest {

	@BeforeEach
	void reset() {
		Context.setGlobal(null);
		Context.forceUpdateGlobal();
	}

	// =========================
	// 基础 scope
	// =========================

	@Test
	void scope_shouldPushAndPop() {
		TestContext ctx = new TestContext("A", false);

		assertNull(Context.getOptional());
		assertEquals(0, Context.getStackSize());

		try (ContextScope ignored = Context.scope(ctx)) {
			assertEquals(ctx, Context.get());
			assertEquals(1, Context.getStackSize());
		}

		assertNull(Context.getOptional());
		assertEquals(0, Context.getStackSize());
	}

	@Test
	void scope_shouldRestoreAfterException() {
		TestContext ctx = new TestContext("A", false);

		assertThrows(RuntimeException.class, () ->
			Context.begin(ctx, () -> {
				throw new RuntimeException("boom");
			}));

		assertNull(Context.getOptional());
		assertEquals(0, Context.getStackSize());
	}

	// =========================
	// begin(...)
	// =========================

	@Test
	void begin_shouldExecuteInsideContext() {
		TestContext ctx = new TestContext("B", false);

		Context.begin(ctx, () ->
			assertEquals(ctx, Context.get()));

		assertNull(Context.getOptional());
	}

	@Test
	void begin_supplierShouldReturnValue() {
		TestContext ctx = new TestContext("C", false);

		String result = Context.begin(ctx, () -> {
			assertEquals(ctx, Context.get());
			return "ok";
		});

		assertEquals("ok", result);
		assertNull(Context.getOptional());
	}

	// =========================
	// inherit
	// =========================

	@Test
	void inheritRunnable_shouldBindContext() {
		TestContext ctx = new TestContext("D", false);

		Runnable wrapped = Context.inherit(ctx, () ->
			assertEquals(ctx, Context.get()));

		assertNull(Context.getOptional());
		wrapped.run();
		assertNull(Context.getOptional());
	}

	@Test
	void inheritSupplier_shouldBindContext() {
		TestContext ctx = new TestContext("E", false);

		Supplier<String> wrapped = Context.inherit(ctx, () -> {
			assertEquals(ctx, Context.get());
			return "value";
		});

		assertEquals("value", wrapped.get());
		assertNull(Context.getOptional());
	}

	// =========================
	// 全局上下文
	// =========================

	@Test
	void global_shouldBeReturnedWhenNoLocalContext() {
		TestContext global = new TestContext("GLOBAL", false);
		Context.setGlobal(global);

		assertNull(Context.getOptional());

		Context.forceUpdateGlobal();
		assertEquals(global, Context.get());
	}

	// =========================
	// Debug 传播
	// =========================

	@Test
	void debug_shouldPropagateFromParent() {
		TestContext parent = new TestContext("P", true);
		TestContext child = new TestContext("C", false);

		try (ContextScope ignored = Context.scope(parent)) {
			try (ContextScope ignored2 = Context.scope(child)) {
				assertTrue(Context.isEffectiveDebug());
			}
		}
	}

	@Test
	void debug_shouldBeFalseWhenNoDebugAnywhere() {
		TestContext ctx = new TestContext("X", false);

		try (ContextScope ignored = Context.scope(ctx)) {
			assertFalse(Context.isEffectiveDebug());
		}
	}

	// =========================
	// getEach / getEachList
	// =========================

	@Test
	void getEach_shouldReturnNearestMatch() {
		TestContext a = new TestContext("A", false);
		TestContext b = new TestContext("B", false);

		try (ContextScope ignored = Context.scope(a)) {
			try (ContextScope ignored2 = Context.scope(b)) {

				Context result = Context.getEach(ctx ->
					((TestContext) ctx).getName().equals("A")
				);

				assertEquals(a, result);
			}
		}
	}

	@Test
	void getEachList_shouldReturnAllMatches() {
		TestContext a1 = new TestContext("A", false);
		TestContext a2 = new TestContext("A", false);

		try (ContextScope ignored = Context.scope(a1)) {
			try (ContextScope ignored2 = Context.scope(a2)) {

				List<Context> list = Context.getEachList(ctx ->
					((TestContext) ctx).getName().equals("A")
				);

				assertEquals(2, list.size());
				assertEquals(a2, list.get(0));
				assertEquals(a1, list.get(1));
			}
		}
	}

	// =========================
	// 多线程隔离
	// =========================

	@Test
	void contexts_shouldBeThreadIsolated() throws Exception {
		TestContext ctx = new TestContext("MAIN", false);

		try (ContextScope ignored = Context.scope(ctx)) {

			ExecutorService executor = Executors.newSingleThreadExecutor();

			Future<Context> future = executor.submit(Context::getOptional);

			Context otherThreadContext = future.get();

			executor.shutdown();

			assertNull(otherThreadContext);
		}
	}

	// =========================
	// scopeDisable
	// =========================

	@Test
	void scopeDisable_shouldHideExistingContext() {
		TestContext ctx = new TestContext("A", false);

		try (ContextScope ignored = Context.scope(ctx)) {
			assertEquals(ctx, Context.get());

			try (ContextScope ignored2 = Context.scopeDisable()) {
				assertNull(Context.getOptional());
			}

			assertEquals(ctx, Context.get());
		}
	}
}
