package io.github.wsyong11.gameforge.framework.dataflow.element;

import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MissingElement implements Element {
	public static final MissingElement INSTANCE = new MissingElement();

	private MissingElement() { /* no-op */ }

	@Nullable
	@Override
	public MutableElement asMutable() {
		return null;
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return obj instanceof MissingElement;
	}

	@Override
	public int hashCode() {
		return MissingElement.class.hashCode();
	}

	@NotNull
	@Override
	public String toString() {
		return "<missing>";
	}
}
