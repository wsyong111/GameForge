package io.github.wsyong11.gameforge.util.collection.tree;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BFSIterator<N extends TreeNode<?, ?>> implements Iterator<N> {
	private final Queue<N> queue;

	public BFSIterator(@NotNull N node) {
		Objects.requireNonNull(node, "node is null");

		this.queue = new ArrayDeque<>();
		this.queue.add(node);
	}

	@Override
	public boolean hasNext() {
		return !this.queue.isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	public N next() {
		if (this.queue.isEmpty())
			throw new NoSuchElementException();

		N current = this.queue.poll();

		List<N> children = current
			.getChildren()
			.stream()
			.map(n -> (N) n)
			.toList();

		this.queue.addAll(children);
		return current;
	}
}
