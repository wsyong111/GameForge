package io.github.wsyong11.gameforge.util.collection;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public interface BoundedList<T> extends List<T> {
	int getMaxSize();

	default boolean isFull(){
		return this.size() == this.getMaxSize();
	}
}
