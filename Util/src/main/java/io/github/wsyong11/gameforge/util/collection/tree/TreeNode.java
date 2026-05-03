package io.github.wsyong11.gameforge.util.collection.tree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TreeNode<K, V> {
	private final K key;
	private V value;

	private TreeNode<K, V> parent;

	private final Map<K, TreeNode<K, V>> children;

	protected TreeNode(@NotNull K key, @Nullable V value) {
		Objects.requireNonNull(key, "key is null");

		this.key = key;
		this.value = value;

		this.parent = null;

		this.children = new LinkedHashMap<>();
	}

	@NotNull
	public K getKey() {
		return this.key;
	}

	@Nullable
	public V getValue() {
		return this.value;
	}

	public void setValue(@Nullable V value) {
		this.value = value;
	}

	@Nullable
	public TreeNode<K, V> getParent() {
		return this.parent;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Nullable
	public TreeNode<K, V> getChild(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");
		return this.children.get(key);
	}

	public boolean containsChild(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");
		return this.children.containsKey(key);
	}

	@Nullable
	public TreeNode<K, V> findChild(@NotNull Predicate<TreeNode<K, V>> predicate) {
		Objects.requireNonNull(predicate, "predicate is null");

		for (TreeNode<K, V> child : this.children.values()) {
			if (predicate.test(child))
				return child;
		}

		return null;
	}

	@NotNull
	@Unmodifiable
	public List<TreeNode<K, V>> findChildren(@NotNull Predicate<TreeNode<K, V>> predicate) {
		Objects.requireNonNull(predicate, "predicate is null");

		return this.children
			.values()
			.stream()
			.filter(predicate)
			.toList();
	}

	@NotNull
	@Unmodifiable
	public List<TreeNode<K, V>> getChildren() {
		return List.copyOf(this.children.values());
	}

	public int childCount() {
		return this.children.size();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private boolean isAncestorOf(@NotNull TreeNode<K, V> node) {
		Objects.requireNonNull(node, "node is null");

		TreeNode<K, V> current = node;
		while (current != null) {
			if (current == this)
				return true;
			current = current.parent;
		}

		return false;
	}

	private void attachChild(@NotNull TreeNode<K, V> node) {
		TreeNode<K, V> existing = this.children.get(node.key);

		if (existing != null && existing != node)
			throw new IllegalStateException("Duplicate key: " + node.key);

		if (existing == node)
			return;

		this.children.put(node.key, node);
		node.parent = this;
	}

	@NotNull
	public TreeNode<K, V> addChild(@NotNull TreeNode<K, V> node) {
		Objects.requireNonNull(node, "node is null");

		if (node == this)
			throw new IllegalStateException("A node cannot be a child of itself");

		if (node.isAncestorOf(this))
			throw new IllegalStateException("Cannot add an ancestor as a child: " + node.key);

		node.detach();
		this.attachChild(node);

		return node;
	}

	@NotNull
	public TreeNode<K, V> addChild(@NotNull K key, @Nullable V value) {
		Objects.requireNonNull(key, "key is null");

		TreeNode<K, V> node = new TreeNode<>(key, value);
		this.attachChild(node);
		return node;
	}

	@Nullable
	public TreeNode<K, V> removeChild(@NotNull TreeNode<K, V> node) {
		Objects.requireNonNull(node, "node is null");

		TreeNode<K, V> currentNode = this.children.get(node.key);
		if (currentNode != node)
			return null;

		this.children.remove(node.key);
		currentNode.parent = null;

		return currentNode;
	}

	@Nullable
	public TreeNode<K, V> removeChild(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");

		TreeNode<K, V> node = this.children.remove(key);
		if (node != null)
			node.parent = null;

		return node;
	}

	public void clearChildren() {
		for (TreeNode<K, V> node : this.children.values())
			node.parent = null;

		this.children.clear();
	}

	public void detach() {
		if (this.parent == null)
			return;

		TreeNode<K, V> parent = this.parent;
		this.parent = null;
		parent.children.remove(this.key, this);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public boolean isLeaf() {
		return this.children.isEmpty();
	}

	public boolean isRoot() {
		return this.parent == null;
	}

	public int getDepth() {
		int depth = 0;
		TreeNode<K, V> current = this.parent;
		while (current != null) {
			depth++;
			current = current.parent;
		}
		return depth;
	}

	@NotNull
	public TreeNode<K, V> getRoot() {
		TreeNode<K, V> current = this;
		while (current.parent != null)
			current = current.parent;
		return current;
	}

	@NotNull
	@Unmodifiable
	public List<TreeNode<K, V>> getPath() {
		List<TreeNode<K, V>> list = new ArrayList<>();
		TreeNode<K, V> current = this;

		while (current != null) {
			list.add(current);
			current = current.parent;
		}

		Collections.reverse(list);
		return Collections.unmodifiableList(list);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public Stream<TreeNode<K, V>> stream() {
		return Stream.concat(
			Stream.of(this),
			this.children
				.values()
				.stream()
				.flatMap(TreeNode::stream)
		);
	}
}
