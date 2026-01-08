package io.github.wsyong11.gameforge.framework.dataflow.element;

import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NullElement implements Element {
	public static final NullElement INSTANCE = new NullElement();

	private NullElement() { /* no-op */ }

	@Nullable
	@Override
	public MutableElement asMutable() {
		return null;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return obj instanceof NullElement;
	}

	@Override
	public int hashCode() {
		return NullElement.class.hashCode();
	}

	@NotNull
	@Override
	public String toString() {
		return "<null>";
	}
}
