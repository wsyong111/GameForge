package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.util.collection.ArrayIterators;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public class ResourcePath implements Iterable<String>, Comparable<ResourcePath> {
	public static final String SEPARATOR = "/";
	public static final char SEPARATOR_CHAR = '/';

	public static final ResourcePath ROOT = new ResourcePath(ArrayUtils.EMPTY_STRING_ARRAY, true);

	@NotNull
	public static ResourcePath of(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");

		if (isRoot(path))
			return ROOT;

		return new ResourcePath(splitPath(path), isDirectory(path));
	}

	private static boolean isRoot(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");

		if (path.length() != 1)
			return false;

		char c = path.charAt(0);
		return c == SEPARATOR_CHAR || c == '\\';
	}

	@NotNull
	public static ResourcePath of(String... paths) {
		Objects.requireNonNull(paths, "paths is null");
		return of(String.join(SEPARATOR, paths));
	}

	@NotNull
	private static String[] splitPath(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");

		if (path.isEmpty() || isRoot(path))
			return ArrayUtils.EMPTY_STRING_ARRAY;

		return Arrays.stream(path.replace('\\', SEPARATOR_CHAR).split(SEPARATOR))
		             .filter(s -> !s.isEmpty())
		             .toArray(String[]::new);
	}

	private static boolean isDirectory(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return path.endsWith(SEPARATOR);
	}

	private final String[] path;
	private final boolean directory;

	@Nullable
	private volatile String fullPath;

	private volatile int hash;

	protected ResourcePath(@NotNull String[] paths, boolean directory) {
		Objects.requireNonNull(paths, "paths is null");

		this.path = paths;
		this.directory = directory;

		this.fullPath = null;
		this.hash = Integer.MAX_VALUE;
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
		if (this.isEmpty())
			return this.toDirectory();

		return new ResourcePath(Arrays.copyOf(this.path, this.path.length - 1), true);
	}

	public boolean isRoot() {
		return this.isEmpty() && this.directory;
	}

	@NotNull
	public ResourcePath subPath(int begin, int end) {
		if (begin < 0 || end > this.path.length || begin >= end)
			throw new ArrayIndexOutOfBoundsException(
				"Invalid sub path range: " + begin + " to " + end +
					", length=" + this.path.length
			);

		if (begin == 0 && end == this.path.length)
			return this;

		String[] sub = Arrays.copyOfRange(this.path, begin, end);

		boolean isDir = (end < this.path.length) || this.directory;
		return new ResourcePath(sub, isDir);
	}

	@NotNull
	public ResourcePath relativize(@NotNull ResourcePath base, boolean strict) {
		Objects.requireNonNull(base, "base is null");

		int max = Math.min(this.path.length, base.path.length);
		int i = 0;
		while (i < max && Objects.equals(this.path[i], base.path[i]))
			i++;

		if (i < base.path.length) {
			if (strict)
				throw new IllegalArgumentException("Base path is not a prefix of this path");

			return this;
		}

		String[] remaining = Arrays.copyOfRange(this.path, i, this.path.length);
		return new ResourcePath(remaining, this.directory);
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
			if (!Objects.equals(this.path[i], paths[i]))
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
			if (!Objects.equals(this.path[offset + i], paths[i]))
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
		return this.resolve(String.join(SEPARATOR, path));
	}

	@NotNull
	public ResourcePath lower() {
		String[] path = new String[this.path.length];
		for (int i = 0; i < this.path.length; i++)
			path[i] = this.path[i].toLowerCase(Locale.ROOT);

		return new ResourcePath(path, this.directory);
	}

	@NotNull
	public ResourcePath upper() {
		String[] path = new String[this.path.length];
		for (int i = 0; i < this.path.length; i++)
			path[i] = this.path[i].toUpperCase(Locale.ROOT);

		return new ResourcePath(path, this.directory);
	}

	@NotNull
	public String indexOf(int index) {
		Objects.checkIndex(index, this.path.length);
		return this.path[index];
	}

	@NotNull
	public String getExtension() {
		if (this.directory)
			return "";

		String name = this.getName();
		int extensionIndex = name.lastIndexOf('.');
		if (extensionIndex == -1)
			return "";

		return name.substring(extensionIndex);
	}

	@NotNull
	public String[] toArray() {
		return Arrays.copyOf(this.path, this.path.length);
	}

	@NotNull
	@Override
	public Iterator<String> iterator() {
		return ArrayIterators.iterator(this.path);
	}

	@Override
	public int compareTo(@NotNull ResourcePath o) {
		Objects.requireNonNull(o, "o is null");

		if (this.isDirectory() && !o.isDirectory())
			return -1;

		if (!this.isDirectory() && o.isDirectory())
			return 1;

		int nameCompare = this.getName().compareTo(o.getName());
		if (nameCompare != 0)
			return nameCompare;

		int length = this.length();
		int otherLength = o.length();

		int len = Math.min(length, otherLength);
		for (int i = 0; i < len; i++) {
			int cmp = this.indexOf(i).compareTo(o.indexOf(i));
			if (cmp != 0)
				return cmp;
		}

		return Integer.compare(length, otherLength);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ResourcePath other = (ResourcePath) o;
		return this.directory == other.directory
			&& Arrays.equals(this.path, other.path);
	}

	@Override
	public int hashCode() {
		if (this.hash == Integer.MAX_VALUE)
			this.hash = Objects.hash(Arrays.hashCode(this.path), this.directory);
		return this.hash;
	}

	@Override
	public String toString() {
		if (this.fullPath == null)
			this.fullPath = String.join(SEPARATOR, this.path)
				+ (this.directory ? SEPARATOR : "");

		return this.fullPath;
	}
}
