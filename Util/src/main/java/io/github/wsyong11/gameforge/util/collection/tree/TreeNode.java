package io.github.wsyong11.gameforge.util.collection.tree;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Predicate;

public class TreeNode<K, V> {
	private final K key;
	private V value;

	private TreeNode<K, V> parent;

	private final Map<K, TreeNode<K, V>> children;

	public TreeNode(@Nullable K key, @Nullable V value) {
		this(key, value, null);
	}

	protected TreeNode(@Nullable K key, @Nullable V value, @Nullable TreeNode<K, V> parent) {
		this.key = key;
		this.value = value;

		this.parent = parent;

		this.children = new LinkedHashMap<>();
	}

	@Nullable
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

	protected void setParent(@Nullable TreeNode<K, V> parent) {
		this.parent = parent;
	}

	@Nullable
	public TreeNode<K, V> getChild(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");
		return this.children.get(key);
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

	public boolean containsChild(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");
		return this.children.containsKey(key);
	}

	@NotNull
	public TreeNode<K, V> addChild(@NotNull TreeNode<K, V> node) {
		Objects.requireNonNull(node, "node is null");

		K key = node.getKey();
		if (key == null)
			throw new IllegalArgumentException("Root node cannot add to children");

		if (this.children.containsValue(node))
			throw new IllegalStateException("Node " + node + " already in children");

		node.detach();
		this.children.put(key, node);
		node.setParent(this);

		return node;
	}

	@NotNull
	public TreeNode<K, V> addChild(@NotNull K key, @Nullable V value) {
		Objects.requireNonNull(key, "key is null");

		if (this.children.containsKey(key))
			throw new IllegalStateException("Key " + key + " already in children");

		TreeNode<K, V> node = new TreeNode<>(key, value, this);
		this.children.put(key, node);
		return node;
	}

	@Nullable
	public TreeNode<K, V> removeChild(@NotNull TreeNode<K, V> node) {
		Objects.requireNonNull(node, "node is null");

		K key = node.getKey();
		if (key == null || !this.children.remove(key, node))
			return null;

		node.detach();
		return node;
	}

	@Nullable
	public TreeNode<K, V> removeChild(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");

		TreeNode<K, V> node = this.children.remove(key);
		if (node == null)
			return null;

		node.detach();
		return node;
	}

	public void clearChildren() {
		for (TreeNode<K, V> node : this.children.values())
			node.detach();
		this.children.clear();
	}

	@NotNull
	@Unmodifiable
	public List<TreeNode<K, V>> getChildren() {
		return List.copyOf(this.children.values());
	}

	public int childCount() {
		return this.children.size();
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

	public boolean isLeaf() {
		return this.children.isEmpty();
	}

	public boolean isRoot() {
		return this.parent == null;
	}

	public void detach() {
		if (this.parent != null) {
			this.parent.removeChild(this);
			this.parent = null;
		}
	}
}
