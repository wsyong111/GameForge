package io.github.wsyong11.gameforge.framework.system.security.permission;

import io.github.wsyong11.gameforge.util.Nameable;
import org.jetbrains.annotations.NotNull;

public abstract class Permission implements Nameable {
	protected Permission() { /* no-op */ }

	@NotNull
	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public String toString() {
		return this.getName();
	}
}
