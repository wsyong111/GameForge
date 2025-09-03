package io.github.wsyong11.gameforge.util.collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Consumer;

/**
 * 多叉树抽象实现
 *
 * @param <K>  键类型
 * @param <V>  值类型
 * @param <NK> 节点键类型
 */
public abstract class AbstractMultiTree<K, V, NK> {
	private final Node root;

	public AbstractMultiTree() {
		this.root = new Node(null, null, null);
		this.root.toBranch();
	}

	/**
	 * 获取根节点
	 *
	 * @return 根节点对象
	 */
	@NotNull
	protected Node getRoot() {
		return this.root;
	}

	/**
	 * 将键拆分为节点键列表
	 *
	 * @param key 键
	 * @return 节点键列表
	 */
	@NotNull
	protected abstract List<NK> splitKey(@NotNull K key);

	/**
	 * 将节点键连接为键
	 *
	 * @param keys 节点键列表
	 * @return 合并后的键
	 */
	@NotNull
	protected abstract K joinKey(@NotNull List<NK> keys);

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 向多叉树添加一个值，如果节点已存在则修改值
	 *
	 * @param key   键
	 * @param value 值
	 * @return 创建或更改的节点
	 */
	protected Node putNode(@NotNull K key, @Nullable V value) {
		Objects.requireNonNull(key, "key is null");
		return this.putNode(this.splitKey(key), value);
	}

	/**
	 * 向多叉树添加一个值，如果节点已存在则修改值
	 *
	 * @param keys  节点键列表
	 * @param value 值
	 * @return 创建或更改的节点
	 */
	@NotNull
	protected Node putNode(@NotNull List<NK> keys, @Nullable V value) {
		Objects.requireNonNull(keys, "keys is null");

		Node current = this.root;

		int size = keys.size();
		for (int i = 0; i < size; i++) {
			NK nodeKey = keys.get(i);
			boolean leaf = (i == size - 1);

			current = current.computeIfAbsentChild(nodeKey, leaf ? value : null);
			if (leaf)
				current.setValue(value);
		}
		return current;
	}

	/**
	 * 从多叉树中获取节点
	 *
	 * @param key 键
	 * @return 多叉树中的节点，如果不存在则返回 null
	 */
	@Nullable
	protected Node getNode(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");
		return this.getNode(this.splitKey(key));
	}

	/**
	 * 从多叉树中获取节点
	 *
	 * @param keys 节点键列表
	 * @return 多叉树中的节点，如果不存在则返回 null
	 */
	@Nullable
	protected Node getNode(@NotNull List<NK> keys) {
		Objects.requireNonNull(keys, "keys is null");

		Node current = this.root;
		for (NK nodeKey : keys) {
			current = current.getChild(nodeKey);
			if (current == null)
				return null;
		}

		return current;
	}

	/**
	 * 从多叉树中删除节点
	 *
	 * @param key 键
	 * @return 从多叉树中移除的节点，如果未删除节点则返回 null
	 */
	protected Node removeNode(@NotNull K key) {
		Objects.requireNonNull(key, "key is null");
		return this.removeNode(this.splitKey(key));
	}

	/**
	 * 从多叉树中删除节点
	 *
	 * @param keys 节点键列表
	 * @return 从多叉树中移除的节点，如果未删除节点则返回 null
	 */
	protected Node removeNode(@NotNull List<NK> keys) {
		Objects.requireNonNull(keys, "keys is null");

		List<NK> nodeKeys = new ArrayList<>(keys);
		NK targetKey = nodeKeys.remove(nodeKeys.size() - 1);

		Node current = this.root;
		for (NK nodeKey : nodeKeys) {
			current = current.getChild(nodeKey);
			if (current == null)
				return null;
		}

		return current.removeChild(targetKey);
	}

	/**
	 * 深度遍历多叉树，按照当前节点到子节点依次遍历
	 *
	 * @param visitor 遍历回调函数
	 */
	protected void traverse(@NotNull Consumer<Node> visitor) {
		Objects.requireNonNull(visitor, "visitor is null");
		this.root.traverse(visitor);
	}

	/**
	 * 键节点转换成字符串
	 *
	 * @param node 节点
	 * @return 节点字符串
	 */
	@NotNull
	protected String nodeToString(@NotNull Node node) {
		Objects.requireNonNull(node, "node is null");
		return "Node{\"" + this.joinKey(node.getFullKeyPath()) + "\"}";
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 多叉树节点
	 */
	protected class Node {
		/* 节点键，如果是根节点则为null */
		@Nullable
		private final NK key;

		/* 节点值，如果是根节点则为null */
		@Nullable
		private V value;

		/* 父节点，如果是根节点为null */
		@Nullable
		private final Node parent;

		/* 子项，如果当前节点是叶子节点则为 null，否则则懒加载，Map实现应该保持添加顺序 */
		@Nullable
		private Map<NK, Node> children;

		/* 完整路径缓存 */
		@Nullable
		private List<NK> cachedPath;

		/**
		 * 创建节点
		 *
		 * @param key    键
		 * @param value  值
		 * @param parent 树枝节点
		 */
		public Node(@Nullable NK key, @Nullable V value, @Nullable Node parent) {
			this.key = key;
			this.value = value;
			this.parent = parent;
		}

		/**
		 * 获取节点键
		 *
		 * @return 节点键，如果是根节点则返回 null
		 */
		@Nullable
		public NK getKey() {
			return this.key;
		}

		/**
		 * 获取节点值
		 *
		 * @return 节点值
		 */
		@Nullable
		public V getValue() {
			return this.value;
		}

		/**
		 * 设定节点值
		 *
		 * @param value 节点值
		 */
		public void setValue(@Nullable V value) {
			this.value = value;
		}

		/**
		 * 检查是否为叶子节点
		 *
		 * @return 是否为叶子节点
		 */
		public boolean isLeaf() {
			return this.children == null;
		}

		/**
		 * 检查是否为根节点
		 *
		 * @return 是否为根节点
		 */
		public boolean isRoot() {
			return this.parent == null;
		}

		/**
		 * 获取父节点
		 *
		 * @return 父节点对象，如果为根节点则返回 null
		 */
		@Nullable
		public Node getParent() {
			return this.parent;
		}

		/**
		 * 将节点转换成叶子节点，需要子节点列表为空
		 *
		 * @throws IllegalStateException 当节点不符合转换条件时抛出
		 */
		public void toLeaf() {
			if (this.isRoot() || (this.children != null && !this.children.isEmpty()))
				throw new IllegalStateException("This node is not empty or not a root node");
			this.children = null;
		}

		/**
		 * 将节点转换成树枝节点
		 */
		public void toBranch() {
			this.children = new LinkedHashMap<>();
		}

		/**
		 * 获取子节点
		 *
		 * @param key 节点键
		 * @return 子节点
		 */
		@Nullable
		public Node getChild(@NotNull NK key) {
			Objects.requireNonNull(key, "key is null");
			return this.children == null ? null : this.children.get(key);
		}

		/**
		 * 创建子节点
		 *
		 * @param childKey   子节点的键
		 * @param childValue 子节点的值
		 * @return 创建的子节点对象
		 */
		@NotNull
		public Node addChild(@NotNull NK childKey, @Nullable V childValue) {
			Objects.requireNonNull(childKey, "childKey is null");
			if (this.children == null)
				this.children = new LinkedHashMap<>();

			if (this.children.containsKey(childKey))
				throw new IllegalArgumentException("Duplicate child key: " + childKey);

			Node child = new Node(childKey, childValue, this);

			assert this.children != null;
			this.children.put(childKey, child);

			return child;
		}

		/**
		 * 获取或创建子节点
		 *
		 * @param childKey   子节点的键
		 * @param childValue 子节点的值
		 * @return 创建的子节点对象
		 */
		@NotNull
		public Node computeIfAbsentChild(@NotNull NK childKey, @Nullable V childValue) {
			Objects.requireNonNull(childKey, "childKey is null");
			if (this.children == null)
				this.children = new LinkedHashMap<>();

			return this.children.computeIfAbsent(childKey, k -> new Node(k, childValue, this));
		}

		/**
		 * 从节点列表中删除节点
		 *
		 * @param key 节点键
		 * @return 删除后的节点，如果未删除节点则返回 null
		 */
		@Nullable
		public Node removeChild(@NotNull NK key) {
			Objects.requireNonNull(key, "key is null");
			if (this.children == null)
				return null;

			Node node = this.children.remove(key);

			if (this.children.isEmpty() && !this.isRoot())
				this.toLeaf();

			return node;
		}

		/**
		 * 获取所有子节点
		 *
		 * @return 子节点列表，如果当前节点为叶子节点则始终返回空列表
		 */
		@NotNull
		public Collection<Node> getChildren() {
			return this.children == null ? Collections.emptyList() : this.children.values();
		}

		/**
		 * 获取节点的完整路径
		 *
		 * @return 只读节点路径列表
		 */
		@Unmodifiable
		@NotNull
		public List<NK> getFullKeyPath() {
			if (this.cachedPath != null)
				return this.cachedPath;

			List<NK> keys = new ArrayList<>();

			Node current = this;
			while (current != null && !current.isRoot()) {
				keys.add(current.getKey());
				current = current.getParent();
			}

			Collections.reverse(keys);

			this.cachedPath = Collections.unmodifiableList(keys);
			return this.cachedPath;
		}

		/**
		 * 遍历所有节点
		 *
		 * @param visitor 回调函数
		 */
		public void traverse(@NotNull Consumer<Node> visitor) {
			Objects.requireNonNull(visitor, "visitor is null");
			visitor.accept(this);
			if (this.children == null)
				return;

			for (Node child : this.children.values())
				child.traverse(visitor);
		}
	}
}

