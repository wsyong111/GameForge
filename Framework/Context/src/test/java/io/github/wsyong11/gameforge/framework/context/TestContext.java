package io.github.wsyong11.gameforge.framework.context;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

class TestContext extends Context {
	private final String name;

	TestContext(@NotNull String name, boolean debug) {
		super(debug);
		Objects.requireNonNull(name, "name is null");
		this.name = name;
	}

	@NotNull
	public String getName() {
		return this.name;
	}
}
