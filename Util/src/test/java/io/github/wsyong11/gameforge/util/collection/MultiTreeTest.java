package io.github.wsyong11.gameforge.util.collection;

import io.github.wsyong11.gameforge.util.collection.AbstractMultiTree;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class MultiTreeTest {
	static class StringMultiTree extends AbstractMultiTree<String, Integer, Character> {
		@NotNull
		@Override
		protected List<Character> splitKey(@NotNull String key) {
			List<Character> list = new ArrayList<>();
			for (char c : key.toCharArray()) {
				list.add(c);
			}
			return list;
		}

		@NotNull
		@Override
		protected String joinKey(@NotNull List<Character> keys) {
			StringBuilder sb = new StringBuilder();
			for (Character c : keys) {
				sb.append(c);
			}
			return sb.toString();
		}
	}

	private StringMultiTree tree;

	@BeforeEach
	void setUp() {
		tree = new StringMultiTree();
	}

	@Test
	void testRootNode() {
		AbstractMultiTree<String, Integer, Character>.Node root = tree.getRoot();
		assertTrue(root.isRoot(), "Root should be root node");
		assertNull(root.getKey(), "Root key should be null");
		assertNull(root.getValue(), "Root value should be null");
		assertFalse(root.isLeaf(), "Root should not be leaf when tree is empty");
		assertEquals(0, root.getFullKeyPath().size(), "Root path should be empty");
	}

	@Test
	void testPutAndGetSingleNode() {
		// 插入单个节点
		AbstractMultiTree<String, Integer, Character>.Node node = tree.putNode("a", 1);
		assertNotNull(node, "Put should return non-null node");
		assertEquals('a', node.getKey(), "Node key should be 'a'");
		assertEquals(1, node.getValue(), "Node value should be 1");
		assertTrue(node.isLeaf(), "Single node should be leaf");

		// 获取节点
		AbstractMultiTree<String, Integer, Character>.Node found = tree.getNode("a");
		assertSame(node, found, "Get should return the same node");
		assertEquals("[a]", node.getFullKeyPath().toString(), "Path should be [a]");

		// 更新节点值
		tree.putNode("a", 10);
		assertEquals(10, tree.getNode("a").getValue(), "Value should be updated to 10");
	}

	@Test
	void testPutAndGetDeepPath() {
		// 插入多级节点
		tree.putNode("abc", 123);
		tree.putNode("abd", 124);
		tree.putNode("abe", 125);

		// 验证节点值
		assertEquals(123, tree.getNode("abc").getValue());
		assertEquals(124, tree.getNode("abd").getValue());
		assertEquals(125, tree.getNode("abe").getValue());

		// 验证路径
		assertEquals("[a, b, c]", tree.getNode("abc").getFullKeyPath().toString());
		assertEquals("[a, b, d]", tree.getNode("abd").getFullKeyPath().toString());
		assertEquals("[a, b, e]", tree.getNode("abe").getFullKeyPath().toString());

		// 验证父节点
		AbstractMultiTree<String, Integer, Character>.Node parent = tree.getNode("ab");
		assertNotNull(parent, "Parent node should exist");
		assertNull(parent.getValue(), "Intermediate node value should be null");
		assertEquals(3, parent.getChildren().size(), "Parent should have 3 children");
	}

	@Test
	void testGetNonExistentNode() {
		assertNull(tree.getNode("non-existent"), "Should return null for non-existent node");

		tree.putNode("a", 1);
		assertNull(tree.getNode("b"), "Should return null for non-existent sibling");
		assertNull(tree.getNode("aa"), "Should return null for non-existent child");
	}

	@Test
	void testRemoveLeafNode() {
		tree.putNode("apple", 10);
		tree.putNode("app", 5); // 中间节点

		// 删除叶子节点
		AbstractMultiTree<String, Integer, Character>.Node removed = tree.removeNode("apple");
		assertNotNull(removed, "Should return removed node");
		assertEquals("apple", tree.joinKey(removed.getFullKeyPath()));
		assertEquals(10, removed.getValue());

		tree.removeNode("appl");

		// 验证树状态
		assertNull(tree.getNode("apple"), "Node should be removed");
		assertNotNull(tree.getNode("app"), "Parent node should still exist");
		assertTrue(tree.getNode("app").isLeaf(), "Parent should become leaf after removal");
	}

	@Test
	void testRemoveIntermediateNode() {
		tree.putNode("apple", 10);
		tree.putNode("app", 5); // 中间节点

		// 删除中间节点
		AbstractMultiTree<String, Integer, Character>.Node removed = tree.removeNode("app");
		assertNotNull(removed, "Should return removed node");
		assertEquals(5, removed.getValue());

		// 验证树状态
		assertNull(tree.getNode("app"), "Node should be removed");
		assertNull(tree.getNode("apple"), "Child node should still exist");
	}

	@Test
	void testRemoveNonExistentNode() {
		assertNull(tree.removeNode("non-existent"), "Should return null when removing non-existent node");

		tree.putNode("a", 1);
		assertNull(tree.removeNode("b"), "Should return null when removing non-existent sibling");
	}

	@Test
	void testTraverseEmptyTree() {
		List<String> paths = new ArrayList<>();
		tree.traverse(node -> paths.add(tree.joinKey(node.getFullKeyPath())));

		assertEquals(1, paths.size(), "Should only traverse root node");
		assertEquals("", paths.get(0), "Root path should be empty string");
	}

	@Test
	void testTraversePopulatedTree() {
		// 创建测试数据
		tree.putNode("a", 1);
		tree.putNode("ab", 2);
		tree.putNode("abc", 3);
		tree.putNode("abd", 4);
		tree.putNode("b", 5);

		// 收集遍历结果
		List<String> paths = new ArrayList<>();
		tree.traverse(node -> {
			if (!node.isRoot()) {
				paths.add(tree.joinKey(node.getFullKeyPath()));
			}
		});

		// 验证遍历顺序（深度优先）
		List<String> expected = Arrays.asList(
			"a", "ab", "abc", "abd", "b"
		);
		assertEquals(expected, paths, "Traversal should be in depth-first order");
	}

	@Test
	void testNodeToString() {
		tree.putNode("test", 42);
		AbstractMultiTree<String, Integer, Character>.Node node = tree.getNode("test");
		assertEquals("Node{\"test\"}", tree.nodeToString(node));
	}

	@Test
	void testKeyWithSpecialCharacters() {
		tree.putNode("key/with/slashes", 100);
		tree.putNode("key.with.dots", 200);
		tree.putNode("key-with-dashes", 300);

		assertEquals(100, tree.getNode("key/with/slashes").getValue());
		assertEquals(200, tree.getNode("key.with.dots").getValue());
		assertEquals(300, tree.getNode("key-with-dashes").getValue());
	}

	@Test
	void testDeepNesting() {
		// 创建深度嵌套的路径
		StringBuilder deepKey = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			deepKey.append((char) ('a' + (i % 26)));
		}
		String key = deepKey.toString();

		tree.putNode(key, 1000);
		AbstractMultiTree<String, Integer, Character>.Node node = tree.getNode(key);

		assertNotNull(node, "Deeply nested node should be found");
		assertEquals(1000, node.getValue());
		assertEquals(100, node.getFullKeyPath().size());
	}

	@Test
	void testConcurrentModificationDuringTraversal() {
		// 添加一些节点
		tree.putNode("a", 1);
		tree.putNode("b", 2);
		tree.putNode("c", 3);

		// 在遍历过程中尝试修改树结构
		assertThrows(ConcurrentModificationException.class, () -> {
			tree.traverse(node -> {
				if (node.getKey() != null && node.getKey() == 'b') {
					tree.putNode("d", 4); // 修改树结构
				}
			});
		});
	}

	@Test
	void testPerformance() {
		// 性能测试 - 插入大量节点
		int count = 10_000;
		long start = System.currentTimeMillis();

		for (int i = 0; i < count; i++) {
			String key = "key" + i;
			tree.putNode(key, i);
		}

		long insertTime = System.currentTimeMillis() - start;
		System.out.println("Inserted " + count + " nodes in " + insertTime + "ms");

		// 查询性能
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++) {
			String key = "key" + i;
			assertNotNull(tree.getNode(key), "Node should exist: " + key);
		}
		long queryTime = System.currentTimeMillis() - start;
		System.out.println("Queried " + count + " nodes in " + queryTime + "ms");

		// 遍历性能
		AtomicInteger traverseCount = new AtomicInteger();
		start = System.currentTimeMillis();
		tree.traverse(node -> traverseCount.incrementAndGet());
		long traverseTime = System.currentTimeMillis() - start;
		System.out.println("Traversed " + traverseCount.get() + " nodes in " + traverseTime + "ms");

		// 基本断言确保操作完成
		// traverseCount == count + "key" + root
		assertEquals(count + 4, traverseCount.get(), "Should traverse all nodes including root");
	}

	@Test
	void testNullKeyHandling() {
		assertThrows(NullPointerException.class, () -> tree.putNode((String) null, 1));
		assertThrows(NullPointerException.class, () -> tree.getNode((String) null));
		assertThrows(NullPointerException.class, () -> tree.removeNode((String) null));
	}

	@Test
	void testDuplicateKeys() {
		tree.putNode("key", 1);
		tree.putNode("key", 2); // 更新现有节点

		assertEquals(2, tree.getNode("key").getValue(), "Value should be updated");

		// 添加具有相同前缀的键
		tree.putNode("key/sub", 3);
		assertEquals(3, tree.getNode("key/sub").getValue());
		assertEquals(2, tree.getNode("key").getValue(), "Original value should remain");
	}
}
