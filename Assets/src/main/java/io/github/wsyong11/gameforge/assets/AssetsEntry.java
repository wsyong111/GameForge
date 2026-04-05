package io.github.wsyong11.gameforge.assets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Objects;

public class AssetsEntry {
	private final String name;
	private final long size;

	private final ClassLoader classLoader;

	AssetsEntry(@NotNull ClassLoader classLoader, @NotNull String name, long size) {
		Objects.requireNonNull(classLoader, "classLoader is null");
		Objects.requireNonNull(name, "name is null");

		this.name = name;
		this.size = size;

		this.classLoader = classLoader;
	}

	@NotNull
	public String getName() {
		return this.name;
	}

	@Nullable
	public InputStream openStream() {
		return this.classLoader.getResourceAsStream(this.name);
	}

	@NotNull
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	public long getSize() {
		return this.size;
	}

	@Override
	public String toString() {
		return "Assets[\"" + this.name + "\", " + this.size + "B]";
	}
}
