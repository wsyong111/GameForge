package io.github.wsyong11.gameforge.framework.lang.ast.node;

import io.github.wsyong11.gameforge.framework.lang.ast.SourceInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public abstract class AbstractASTNode<THIS extends AbstractASTNode<THIS>> implements ASTNode {
	private boolean frozen;

	private List<ASTNode> children;
	private ASTNode parent;
	private SourceInfo source;

	protected AbstractASTNode(@NotNull List<ASTNode> defaultChildren) {
		this();
		Objects.requireNonNull(defaultChildren, "defaultChildren is null");

		this.children = new ArrayList<>(defaultChildren);
	}

	public AbstractASTNode() {
		this.frozen = false;

		this.children = null;
		this.parent = null;
	}

	@NotNull
	protected abstract THIS copySelf(@NotNull List<ASTNode> clonedChildren);

	/**
	 * 子类可以覆盖此方法，决定是否允许添加子节点
	 */
	protected boolean hasChildren() {
		return true;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void assertFrozen() {
		if (this.frozen)
			throw new IllegalStateException("This node is frozen");
	}

	@NotNull
	protected List<ASTNode> ensureChildrenList() {
		if (this.children == null)
			this.children = new ArrayList<>();
		return this.children;
	}

	@Nullable
	@Override
	public ASTNode getParent() {
		return this.parent;
	}

	protected void setParent(@Nullable ASTNode parent) {
		this.assertFrozen();
		this.parent = parent;
	}

	public void setSource(@Nullable SourceInfo source) {
		this.assertFrozen();
		this.source = source;
	}

	@NotNull
	@UnmodifiableView
	@Override
	public List<ASTNode> getChildren() {
		if (this.children == null)
			return List.of();

		// Children list is frozen in freeze method
		if (this.frozen)
			return this.children;

		return Collections.unmodifiableList(this.children);
	}

	private void preAddChild(@NotNull ASTNode node) {
		Objects.requireNonNull(node, "node is null");

		if (!this.hasChildren()) {
            throw new UnsupportedOperationException(
                "This node type cannot have children: " + this.getClass().getSimpleName()
            );
        }

		if (node.isFrozen())
			throw new IllegalArgumentException("Node is frozen");

		if (node instanceof AbstractASTNode<?> astNode) {
			ASTNode nodeParent = astNode.getParent();
			if (nodeParent == this)
				return;

			if (nodeParent != null)
				throw new IllegalArgumentException("Node have parent");

			astNode.setParent(this);
		}
	}

	@Override
	public void addNode(@NotNull ASTNode node) {
		Objects.requireNonNull(node, "node is null");

		this.assertFrozen();
		this.preAddChild(node);

		List<ASTNode> children = this.ensureChildrenList();
		children.add(node);
	}

	@NotNull
	@Override
	public ASTNode setNode(int index, @NotNull ASTNode node) {
		Objects.requireNonNull(node, "node is null");

		if (index < 0)
			throw new IndexOutOfBoundsException("Index cannot be negative, i = " + index);

		this.assertFrozen();
		this.preAddChild(node);

		List<ASTNode> children = this.ensureChildrenList();
		ASTNode oldNode = children.set(index, node);
		if (oldNode instanceof AbstractASTNode<?> astNode)
			astNode.setParent(null);

		return oldNode;
	}

	@Override
	public void removeNode(@Nullable ASTNode node) {
		if (node == null)
			return;

		this.assertFrozen();

		if (this.children == null)
			return;

		if (this.children.remove(node) && node instanceof AbstractASTNode<?> astNode)
			astNode.setParent(null);
	}

	@Nullable
	@Override
	public SourceInfo getSource() {
		return this.source;
	}

	@Override
	public void freeze() {
		if (this.frozen)
			return;
		this.frozen = true;

		if (this.children == null) {
			this.children = List.of();
			return;
		}

		this.children = Collections.unmodifiableList(this.children);

		for (ASTNode children : this.children)
			children.freeze();
	}

	@Override
	public boolean isFrozen() {
		return this.frozen;
	}

	@NotNull
	@Override
	public THIS deepCopy() {
		List<ASTNode> clonedChildren = this
			.getChildren()
			.stream()
			.map(ASTNode::deepCopy)
			.toList();

		THIS cloned = this.copySelf(clonedChildren);
		cloned.setSource(this.getSource());

		return cloned;
	}

	@NotNull
	@Override
	public Iterator<ASTNode> iterator() {
		return this.children == null ? Collections.emptyIterator() : this.children.iterator();
	}
}
