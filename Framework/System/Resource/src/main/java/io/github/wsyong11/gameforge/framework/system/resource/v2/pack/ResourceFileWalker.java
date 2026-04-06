package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ResourceFileWalker {
	private final ResourceFileSystem fs;
	private final int maxDepth;

	public ResourceFileWalker(@NotNull ResourceFileSystem fs, int maxDepth) {
		Objects.requireNonNull(fs, "fs is null");

		if (maxDepth < 0)
			throw new IllegalArgumentException("Max depth cannot be negative");

		this.fs = fs;
		this.maxDepth = maxDepth;

		this.closed = false;
	}

	public Result walk() {

	}

	public static class Result {

	}
}
