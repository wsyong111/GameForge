package io.github.wsyong11.gameforge.util.collection;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class RewindableIterator<T> implements Iterator<T> {
	private final Iterator<T> source;
	private final List<T> history;
	private int cursor;

	public RewindableIterator(@NotNull Iterator<T> source) {
		Objects.requireNonNull(source, "source is null");

		this.source = source;
		this.history = new ArrayList<>();
		this.cursor = 0;
	}

	@Override
	public boolean hasNext() {
		return this.cursor < this.history.size() || this.source.hasNext();
	}

	@Override
	public T next() {
		if (this.cursor < this.history.size())
			return this.history.get(this.cursor++);

		T value = this.source.next();
		this.history.add(value);
		this.cursor++;
		return value;
	}

	public boolean canRewind() {
		return this.cursor > 0;
	}

	public void rewind() {
		if (this.cursor == 0)
			throw new IllegalStateException("Cannot rewind beyond start");

		this.cursor--;
	}

	@NotNull
	public T getHistory(int offset) {
		int index = this.cursor - offset;
		Objects.checkIndex(index, this.history.size());
		return this.history.get(index);
	}
}

