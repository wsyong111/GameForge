package io.github.wsyong11.gameforge.framework.dataflow.element;

public final class MissingElement implements Element {
	public static final MissingElement INSTANCE = new MissingElement();

	private MissingElement() { /* no-op */ }

	@Override
	public boolean equals(Object obj) {
		return obj instanceof MissingElement;
	}

	@Override
	public int hashCode() {
		return MissingElement.class.hashCode();
	}

	@Override
	public String toString() {
		return "<missing>";
	}
}
