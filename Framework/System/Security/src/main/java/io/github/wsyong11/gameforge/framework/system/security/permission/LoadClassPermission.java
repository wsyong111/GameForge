package io.github.wsyong11.gameforge.framework.system.security.permission;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LoadClassPermission extends DynamicPermission<LoadClassPermission> {
	private final String name;

	public LoadClassPermission(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		this.name = name;
	}

	@NotNull
	public String getClassName() {
		return this.name;
	}

	@Override
	protected boolean dataEquals(@NotNull LoadClassPermission o) {
		return Objects.equals(this.name, o.name);
	}

	@Override
	protected int dataHashCode() {
		return this.name.hashCode();
	}
}
