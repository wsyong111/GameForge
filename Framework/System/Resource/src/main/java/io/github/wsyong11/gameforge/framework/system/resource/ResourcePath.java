package io.github.wsyong11.gameforge.framework.system.resource;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class ResourcePath {
	public static final String separator = "/";
	public static final char separatorChar = '/';

	@NotNull
	public static ResourcePath of(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return new ResourcePath(path);
	}

	@NotNull
	public static ResourcePath of(String... paths) {
		Objects.requireNonNull(paths, "paths is null");
		return new ResourcePath(String.join(separator, paths));
	}

	@NotNull
	private static String[] splitPath(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");

		if (path.isEmpty())
			return ArrayUtils.EMPTY_STRING_ARRAY;

		return Arrays.stream(path.replace('\\', separatorChar).split(separator))
		             .filter(s -> !s.isEmpty())
		             .toArray(String[]::new);
	}

	private static boolean isDirectory(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return path.endsWith(separator);
	}

	private final String[] path;
	private final boolean directory;

	@Nullable
	private volatile String fullPath;

	protected ResourcePath(@NotNull String[] paths, boolean directory) {
		Objects.requireNonNull(paths, "paths is null");

		this.path = paths;
		this.directory = directory;

		this.fullPath = null;
	}

	protected ResourcePath(@NotNull String path) {
		this(splitPath(path), isDirectory(path));
	}

	public boolean isDirectory() {
		return this.directory;
	}

	@NotNull
	public ResourcePath toDirectory() {
		return this.directory ? this : new ResourcePath(this.path, true);
	}

	@NotNull
	public ResourcePath toFile() {
		return this.directory ? new ResourcePath(this.path, false) : this;
	}

	public boolean isEmpty() {
		return this.path.length == 0;
	}

	public int length() {
		return this.path.length;
	}

	@NotNull
	public String getName() {
		if (this.isEmpty())
			return "";

		return this.path[this.path.length - 1];
	}

	@NotNull
	public ResourcePath parent() {
		if (this.path.length == 0)
			return this.toDirectory();

		return new ResourcePath(Arrays.copyOf(this.path, this.path.length - 1), true);
	}

	public boolean startsWith(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return this.startsWith(splitPath(path));
	}

	public boolean startsWith(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.startsWith(path.path);
	}

	private boolean startsWith(@NotNull String[] paths) {
		Objects.requireNonNull(paths, "paths is null");

		if (paths.length > this.path.length)
			return false;

		for (int i = 0; i < paths.length; i++) {
			if (!this.path[i].equals(paths[i]))
				return false;
		}

		return true;
	}

	public boolean endsWith(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return this.endsWith(splitPath(path));
	}

	public boolean endsWith(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.endsWith(path.path);
	}

	private boolean endsWith(@NotNull String[] paths) {
		Objects.requireNonNull(paths, "paths is null");

		if (paths.length > this.path.length)
			return false;

		int offset = this.path.length - paths.length;
		for (int i = 0; i < paths.length; i++) {
			if (!this.path[offset + i].equals(paths[i]))
				return false;
		}

		return true;
	}

	@NotNull
	public ResourcePath resolve(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return new ResourcePath(ArrayUtils.addAll(this.path, splitPath(path)), isDirectory(path));
	}

	@NotNull
	public ResourcePath resolve(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return new ResourcePath(ArrayUtils.addAll(this.path, path.path), path.directory);
	}

	@NotNull
	public ResourcePath resolve(@NotNull String... path) {
		Objects.requireNonNull(path, "path is null");
		// FIX: resolve("path/to", "the", "path")
		return this.resolve(String.join(separator, path));
	}

	@NotNull
	public String[] toArray() {
		return Arrays.copyOf(this.path, this.path.length);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ResourcePath other = (ResourcePath) o;
		return this.directory == other.directory
			&& Objects.deepEquals(this.path, other.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(this.path), this.directory);
	}

	@Override
	public String toString() {
		if (this.fullPath == null)
			this.fullPath = String.join(separator, this.path)
				 + (this.path.length == 0 || this.directory ? separator : "");

		return this.fullPath;
	}
}
