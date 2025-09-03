package io.github.wsyong11.gameforge.framework.system.security.permission;

import org.jetbrains.annotations.NotNull;

public abstract class DynamicPermission<T extends DynamicPermission<T>> extends Permission {
	public DynamicPermission() { /* no-op */ }

	protected abstract boolean dataEquals(@NotNull T o);

	protected abstract int dataHashCode();

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (this.getClass() != obj.getClass())
			return false;

		return this.dataEquals((T) obj);
	}

	@Override
	public int hashCode() {
		return this.dataHashCode();
	}
}
