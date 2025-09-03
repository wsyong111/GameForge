package io.github.wsyong11.gameforge.framework.system.security;

import org.jetbrains.annotations.Nullable;

public class TrustedClassLoader extends SecurityClassLoader {
	static {
		registerAsParallelCapable();
	}

	public TrustedClassLoader(@Nullable String name, @Nullable ClassLoader parent) {
		super(name, parent);
	}

	public TrustedClassLoader(@Nullable ClassLoader parent) {
		this(null, parent);
	}

	public TrustedClassLoader() {
		this(null);
	}
}
