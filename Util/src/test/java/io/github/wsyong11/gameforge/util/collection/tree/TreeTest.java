package io.github.wsyong11.gameforge.util.collection.tree;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TreeTest {
	@NotNull
	private StringTree newTree() {
		return new StringTree("root");
	}

	@Test
	void testPutAndGet() {
		StringTree tree = newTree();

		assertNull(tree.put("a.b", "hello"));
		assertEquals("hello", tree.get("a.b"));
	}

	@Test
	void testPutOverwrite() {
		StringTree tree = newTree();

		tree.put("a.b", "1");
		String old = tree.put("a.b", "2");

		assertEquals("1", old);
		assertEquals("2", tree.get("a.b"));
	}

	@Test
	void testContainsKey() {
		StringTree tree = newTree();

		tree.put("x.y", "v");

		assertTrue(tree.containsKey("x.y"));
		assertFalse(tree.containsKey("x"));
		assertFalse(tree.containsKey("not.exist"));
	}

	@Test
	void testRemove() {
		StringTree tree = newTree();

		tree.put("a.b.c", "123");

		assertEquals("123", tree.remove("a.b.c"));
		assertNull(tree.get("a.b.c"));
		assertFalse(tree.containsKey("a.b.c"));
	}

	@Test
	void testSize() {
		StringTree tree = newTree();

		assertEquals(0, tree.size());

		tree.put("a", "1");
		tree.put("a.b", "2");

		assertEquals(2, tree.size());

		tree.remove("a");
		assertEquals(1, tree.size());
	}

	@Test
	void testClear() {
		StringTree tree = newTree();

		tree.put("a.b", "1");
		tree.put("x.y", "2");

		tree.clear();

		assertEquals(0, tree.size());
		assertTrue(tree.isEmpty());
		assertNull(tree.get("a.b"));
	}

	@Test
	void testPrune() {
		StringTree tree = newTree();

		tree.put("a.b.c", "1");

		tree.remove("a.b.c");

		// a.b.c 删除后，a 和 b 也应该被剪掉
		assertFalse(tree.containsKey("a.b.c"));
		assertFalse(tree.containsKey("a.b"));
	}

	@Test
	void testDFSOrder() {
		StringTree tree = newTree();

		tree.put("a", "1");
		tree.put("a.b", "2");
		tree.put("c", "3");

		List<String> keys = new ArrayList<>();
		tree.dfs().forEachRemaining(e -> keys.add(e.getKey()));

		assertEquals(List.of("root.a", "root.a.b", "root.c"), keys);
	}

	@Test
	void testBFSOrder() {
		StringTree tree = newTree();

		tree.put("a", "1");
		tree.put("a.b", "2");
		tree.put("c", "3");

		List<String> keys = new ArrayList<>();
		tree.bfs().forEachRemaining(e -> keys.add(e.getKey()));

		assertEquals(List.of("root.a", "root.c", "root.a.b"), keys);
	}
}
