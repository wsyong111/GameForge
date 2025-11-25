package io.github.wsyong11.gameforge.framework.dataflow.element;

public final class NullElement implements Element {
	public static final NullElement INSTANCE = new NullElement();

	private NullElement() { /* no-op */ }

	@Override
	public boolean equals(Object obj) {
		return obj instanceof NullElement;
	}

	@Override
	public int hashCode() {
		return NullElement.class.hashCode();
	}

	@Override
	public String toString() {
		return "null";
	}
}
