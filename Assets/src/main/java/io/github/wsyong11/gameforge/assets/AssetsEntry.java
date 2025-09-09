package io.github.wsyong11.gameforge.assets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.util.Objects;

public class AssetsEntry {
	private final String name;

	private final ClassLoader classLoader;
	private final long size;

	AssetsEntry(@NotNull ClassLoader classLoader, @NotNull String name, long size) {
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

	public long getSize() {
		return this.size;
	}

	@Override
	public String toString() {
		return "Assets[\"" + this.name + "\", " + this.size + "B]";
	}
}
