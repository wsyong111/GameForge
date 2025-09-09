package io.github.wsyong11.gameforge.framework.system.security;

import org.jetbrains.annotations.Nullable;

public class SecurityClassLoader extends ClassLoader {
	static {
		registerAsParallelCapable();
	}

	public SecurityClassLoader(@Nullable String name, @Nullable ClassLoader parent) {
		super(name, parent);
	}

	public SecurityClassLoader(@Nullable ClassLoader parent) {
		this(null, parent);
	}

	public SecurityClassLoader() {
		this(null);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return super.findClass(name);
	}
}
