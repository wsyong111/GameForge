package io.github.wsyong11.gameforge.util.collection.tree;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class Tree<K, V, NK> {
	private final Node root;

	private int size;

	protected Tree(@NotNull NK rootKey) {
		Objects.requireNonNull(rootKey, "rootKey is null");
		this.root = new Node(rootKey);

		this.size = 0;
	}

	@NotNull
	protected abstract List<NK> getNodeKeys(@NotNull K key);

	@NotNull
	protected abstract K getKey(@NotNull List<NK> nodeKeys);

	@Nullable
	@Contract("_, true -> !null")
	private Node getNode(@NotNull List<NK> keys, boolean autoCreate) {
		Objects.requireNonNull(keys, "keys is null");

		Node current = this.root;
		for (NK k : keys) {
			Node child = current.children.get(k);

			if (child == null) {
				if (!autoCreate)
					return null;

				child = new Node(k);
				child.parent = current;
				current.children.put(k, child);
			}

			current = child;
		}

		return current;
	}

	private void prune(@NotNull Node node) {
		Objects.requireNonNull(node, "node is null");

		Node current = node;

		while (current != this.root && !current.present && current.children.isEmpty()) {
			Node parent = current.parent;
			if (parent == null) {
				break;
			}

			parent.children.remove(current.key);

			// 当前节点已经断开，缓存要失效，避免幽灵 key
			current.parent = null;
			current.fullKey = null;

			current = parent;
		}
	}

	private void cleanupSubtree(@NotNull Node node) {
		Objects.requireNonNull(node, "node is null");

		Deque<Node> stack = new ArrayDeque<>();
		stack.push(node);

		while (!stack.isEmpty()) {
			Node current = stack.pop();

			for (Node child : current.children.values()) {
				stack.push(child);
			}

			current.children.clear();
			current.parent = null;
			current.value = null;
			current.present = false;
			current.fullKey = null;
		}
	}

	public int size() {
		return this.size;
	}

	public boolean isEmpty() {
		return this.size == 0;
	}

	public boolean containsKey(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");

		List<NK> keys = this.getNodeKeys(key);
		Node node = this.getNode(keys, false);
		return node != null && node.present;
	}

	@Nullable
	public V get(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");

		List<NK> keys = this.getNodeKeys(key);
		Node node = this.getNode(keys, false);
		if (node == null || !node.present)
			return null;

		return node.value;
	}

	@Nullable
	public V put(@NotNull K key, @Nullable V value) {
		Objects.requireNonNull(key, "key is null");

		List<NK> keys = this.getNodeKeys(key);
		Node node = this.getNode(keys, true);

		V oldValue = node.present ? node.value : null;
		if (!node.present) {
			node.present = true;
			this.size++;
		}

		node.value = value;
		return oldValue;
	}

	@Nullable
	public V remove(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");

		List<NK> keys = this.getNodeKeys(key);
		Node node = this.getNode(keys, false);
		if (node == null || !node.present)
			return null;

		V removedValue = node.value;

		node.present = false;
		node.value = null;
		this.size--;

		this.prune(node);

		return removedValue;
	}

	public void clear() {
		for (Node child : this.root.children.values()) {
			this.cleanupSubtree(child);
		}

		this.root.children.clear();
		this.root.value = null;
		this.root.present = false;
		this.root.fullKey = null;

		this.size = 0;
	}

	@NotNull
	public Iterator<Map.Entry<K, V>> dfs() {
		return new DFSIterator(this.root);
	}

	@NotNull
	public Stream<Map.Entry<K, V>> dfsStream() {
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(this.dfs(), 0),
			false
		);
	}

	@NotNull
	public Iterator<Map.Entry<K, V>> bfs() {
		return new BFSIterator(this.root);
	}

	@NotNull
	public Stream<Map.Entry<K, V>> bfsStream() {
		return StreamSupport.stream(
			Spliterators.spliteratorUnknownSize(this.bfs(), 0),
			false
		);
	}

	private final class Node {
		public final NK key;
		public V value;

		public boolean present;

		public Node parent;

		public final Map<NK, Node> children;

		private volatile K fullKey;

		public Node(@NotNull NK key) {
			Objects.requireNonNull(key, "keys is null");

			this.key = key;
			this.value = null;
			this.present = false;
			this.parent = null;
			this.children = new LinkedHashMap<>();

			this.fullKey = null;
		}

		@NotNull
		private K getFullKey() {
			K cached = this.fullKey;
			if (cached != null)
				return cached;

			Deque<NK> keys = new ArrayDeque<>();

			Node current = this;
			while (current != null) {
				keys.addFirst(current.key);
				current = current.parent;
			}
			this.fullKey = Tree.this.getKey(List.copyOf(keys));

			return this.fullKey;
		}
	}

	private class DFSIterator implements Iterator<Map.Entry<K, V>> {
		private final Deque<Node> stack;
		private Map.Entry<K, V> next;

		public DFSIterator(@NotNull Node node) {
			Objects.requireNonNull(node, "node is null");

			this.stack = new ArrayDeque<>();
			this.stack.push(node);
			this.advance();
		}

		private void advance() {
			this.next = null;

			while (!this.stack.isEmpty()) {
				Node current = this.stack.pop();

				List<Node> children = List.copyOf(current.children.values());
				for (int i = children.size() - 1; i >= 0; i--)
					this.stack.push(children.get(i));

				if (current.present) {
					this.next = new AbstractMap.SimpleImmutableEntry<>(current.getFullKey(), current.value);
					return;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		@NotNull
		@Override
		public Map.Entry<K, V> next() {
			if (this.next == null)
				throw new NoSuchElementException();

			Map.Entry<K, V> result = this.next;
			this.advance();
			return result;
		}
	}

	private class BFSIterator implements Iterator<Map.Entry<K, V>> {
		private final Queue<Node> queue;
		private Map.Entry<K, V> next;

		public BFSIterator(@NotNull Node node) {
			Objects.requireNonNull(node, "node is null");

			this.queue = new ArrayDeque<>();
			this.queue.add(node);
			this.advance();
		}

		private void advance() {
			this.next = null;

			while (!this.queue.isEmpty()) {
				Node current = this.queue.poll();
				this.queue.addAll(current.children.values());

				if (current.present) {
					this.next = new AbstractMap.SimpleImmutableEntry<>(current.getFullKey(), current.value);
					return;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		@NotNull
		@Override
		public Map.Entry<K, V> next() {
			if (this.next == null)
				throw new NoSuchElementException();

			Map.Entry<K, V> result = this.next;
			this.advance();
			return result;
		}
	}
}
