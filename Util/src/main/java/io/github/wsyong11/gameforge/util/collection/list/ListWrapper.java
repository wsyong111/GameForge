package io.github.wsyong11.gameforge.util.collection.list;

import io.github.wsyong11.gameforge.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * @param <T>
 * @deprecated Using {@link com.google.common.collect.ForwardingList}
 */
@Deprecated
public class ListWrapper<T> extends Wrapper<List<T>> implements List<T> {
	public ListWrapper() {
	}

	public ListWrapper(@Nullable List<T> delegate) {
		super(delegate);
	}

	@Override
	public int size() {
		return this.delegate().size();
	}

	@Override
	public boolean isEmpty() {
		return this.delegate().isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return this.delegate().contains(o);
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return this.delegate().iterator();
	}

	@Override
	public void forEach(Consumer<? super T> action) {
		this.delegate().forEach(action);
	}

	@Override
	public Object @NotNull [] toArray() {
		return this.delegate().toArray();
	}

	@Override
	public <T1> T1 @NotNull [] toArray(T1 @NotNull [] a) {
		return this.delegate().toArray(a);
	}

	@Override
	public <T1> T1[] toArray(IntFunction<T1[]> generator) {
		return this.delegate().toArray(generator);
	}

	@Override
	public boolean add(T t) {
		return this.delegate().add(t);
	}

	@Override
	public boolean remove(Object o) {
		return this.delegate().remove(o);
	}

	@SuppressWarnings("SlowListContainsAll")
	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return this.delegate().containsAll(c);
	}

	@Override
	public boolean addAll(@NotNull Collection<? extends T> c) {
		return this.delegate().addAll(c);
	}

	@Override
	public boolean addAll(int index, @NotNull Collection<? extends T> c) {
		return this.delegate().addAll(index, c);
	}

	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		return this.delegate().removeAll(c);
	}

	@Override
	public boolean removeIf(Predicate<? super T> filter) {
		return this.delegate().removeIf(filter);
	}

	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		return this.delegate().retainAll(c);
	}

	@Override
	public void replaceAll(UnaryOperator<T> operator) {
		this.delegate().replaceAll(operator);
	}

	@Override
	public void sort(Comparator<? super T> c) {
		this.delegate().sort(c);
	}

	@Override
	public void clear() {
		this.delegate().clear();
	}

	@Override
	public T get(int index) {
		return this.delegate().get(index);
	}

	@Override
	public T set(int index, T element) {
		return this.delegate().set(index, element);
	}

	@Override
	public void add(int index, T element) {
		this.delegate().add(index, element);
	}

	@Override
	public T remove(int index) {
		return this.delegate().remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.delegate().indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.delegate().lastIndexOf(o);
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator() {
		return this.delegate().listIterator();
	}

	@NotNull
	@Override
	public ListIterator<T> listIterator(int index) {
		return this.delegate().listIterator(index);
	}

	@NotNull
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return this.delegate().subList(fromIndex, toIndex);
	}

	@Override
	public Spliterator<T> spliterator() {
		return this.delegate().spliterator();
	}

	@Override
	public Stream<T> stream() {
		return this.delegate().stream();
	}

	@Override
	public Stream<T> parallelStream() {
		return this.delegate().parallelStream();
	}
}
