package io.github.wsyong11.gameforge.util.collection.tree;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class DFSIterator<N extends TreeNode<?, ?>> implements Iterator<N> {
	private final Deque<N> stack;

	public DFSIterator(@NotNull N node) {
		Objects.requireNonNull(node, "node is null");

		this.stack = new ArrayDeque<>();
		this.stack.add(node);
	}

	@Override
	public boolean hasNext() {
		return !this.stack.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public N next() {
		if (this.stack.isEmpty())
			throw new NoSuchElementException();

		N current = this.stack.pop();

		List<? extends TreeNode<?, ?>> children = new ArrayList<>(current.getChildren());

		for (int i = children.size() - 1; i >= 0; i--) {
			N child = (N) children.get(i);
			this.stack.push(child);
		}

		return current;
	}
}
