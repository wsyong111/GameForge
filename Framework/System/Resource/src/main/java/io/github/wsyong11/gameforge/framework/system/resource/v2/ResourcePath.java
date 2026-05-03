package io.github.wsyong11.gameforge.framework.system.resource.v2;

import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

public class ResourcePath implements Iterable<String>, Comparable<ResourcePath> {
	public static final String SEPARATOR = "/";
	public static final char SEPARATOR_CHAR = '/';

	public static final ResourcePath ROOT = new ResourcePath(ArrayUtils.EMPTY_STRING_ARRAY, true);
	public static final ResourcePath EMPTY = new ResourcePath(ArrayUtils.EMPTY_STRING_ARRAY, false);

	@NotNull
	public static ResourcePath of(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");

		int length = path.length();
		if (length == 0)
			return EMPTY;

		boolean dir = isSeparator(path.charAt(length - 1));

		if (length == 1 && dir)
			return ROOT;

		String[] segments = splitPath(path);
		return new ResourcePath(segments, dir);
	}

	@NotNull
	public static ResourcePath of(String... paths) {
		Objects.requireNonNull(paths, "paths is null");
		return of(String.join(SEPARATOR, paths));
	}

	@NotNull
	public static ResourcePath of(@NotNull Iterable<String> paths) {
		Objects.requireNonNull(paths, "paths is null");
		return of(String.join(SEPARATOR, paths));
	}

	private static boolean isSeparator(char c) {
		return c == SEPARATOR_CHAR
			|| c == '\\'; // FIX: Support old style
	}

	@NotNull
	private static String[] splitPath(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");

		int length = path.length();
		if (length == 0)
			return ArrayUtils.EMPTY_STRING_ARRAY;

		int segmentCount = 0;
		boolean inSegment = false;
		for (int i = 0; i < length; i++) {
			char c = path.charAt(i);

			if (isSeparator(c)) {
				inSegment = false;
			} else if (!inSegment) {
				segmentCount++;
				inSegment = true;
			}
		}

		String[] segments = new String[segmentCount];

		int i = 0;
		int segmentIndex = 0;
		while (i < length) {
			while (i < length && isSeparator(path.charAt(i)))
				i++;

			if (i >= length)
				break;

			int start = i;
			while (i < length && !isSeparator(path.charAt(i)))
				i++;

			String segment = path.substring(start, i);
			if ("..".equals(segment) || ".".equals(segment))
				throw new IllegalArgumentException("Relative segments are not allowed");

			segments[segmentIndex++] = segment;
		}

		return segments;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final String[] segments;
	private final boolean dir;

	protected ResourcePath(@NotNull String[] segments, boolean dir) {
		Objects.requireNonNull(segments, "segments is null");

		this.segments = segments;
		this.dir = dir;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public boolean isDirectory() {
		return this.dir;
	}

	@NotNull
	public ResourcePath toDirectory() {
		return this.dir ? this : new ResourcePath(this.segments, true);
	}

	@NotNull
	public ResourcePath toFile() {
		return !this.dir ? this : new ResourcePath(this.segments, false);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public boolean isRoot() {
		return this == ROOT
			|| (this.isEmpty() && this.dir);
	}

	public int length() {
		return this.segments.length;
	}

	// 目录深度
	public int depth() {
		if (!this.dir)
			return this.segments.length - 1;

		return this.segments.length;
	}

	public boolean isEmpty() {
		return this.segments.length == 0;
	}

	@NotNull
	public ResourcePath parent() {
		if (this.isEmpty())
			return this;

		if (this.segments.length == 1)
			return ROOT;

		String[] newSegment = Arrays.copyOf(this.segments, this.segments.length - 1);
		return new ResourcePath(newSegment, true);
	}

	@NotNull
	public String indexOf(int index) {
		Objects.checkIndex(index, this.segments.length);
		return this.segments[index];
	}

	public int indexOf(@NotNull String segment) {
		Objects.requireNonNull(segment, "segment is null");
		return ArrayUtils.indexOf(this.segments, segment);
	}

	public int lastIndexOf(@NotNull String segment) {
		Objects.requireNonNull(segment, "segment is null");
		return ArrayUtils.lastIndexOf(this.segments, segment);
	}

	public boolean contains(@NotNull String segment) {
		Objects.requireNonNull(segment, "segment is null");
		return ArrayUtils.contains(this.segments, segment);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public String getName() {
		if (this.isEmpty())
			return "";

		return this.segments[this.segments.length - 1];
	}

	@NotNull
	public String getBaseName() {
		String name = this.getName();
		if (name.isEmpty())
			return "";

		int index = name.lastIndexOf('.');
		if (index == -1)
			return name;

		return name.substring(0, index);
	}

	@NotNull
	public String getExtension() {
		String name = this.getName();
		int index = name.lastIndexOf('.');
		if (index == -1)
			return "";

		return name.substring(index + 1);
	}

	public boolean hasExtension() {
		return this.getName().lastIndexOf('.') != -1;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public ResourcePath subPath(int begin, int end) {
		if (begin < 0 || end > this.segments.length || begin >= end)
			throw new IndexOutOfBoundsException(
				"Invalid sub path range: " + begin + " to " + end +
					", length=" + this.segments.length
			);

		if (begin == 0 && end == this.segments.length)
			return this;

		String[] sub = Arrays.copyOfRange(this.segments, begin, end);

		boolean isDir = this.dir || (end < this.segments.length);
		return new ResourcePath(sub, isDir);
	}

	@NotNull
	public ResourcePath subPath(int begin) {
		return this.subPath(begin, this.segments.length);
	}

	public boolean isSubPathOf(@NotNull ResourcePath other) {
		Objects.requireNonNull(other, "other is null");
		return this.startsWith(other);
	}

	@Contract("_, true -> !null")
	@Nullable
	public ResourcePath relativeToPrefix(@NotNull ResourcePath base, boolean strict) {
		Objects.requireNonNull(base, "base is null");

		int max = Math.min(this.segments.length, base.segments.length);
		int i = 0;
		while (i < max && Objects.equals(this.segments[i], base.segments[i]))
			i++;

		if (i < base.segments.length) {
			if (strict)
				throw new IllegalArgumentException("Base path is not a prefix of this path");

			return null;
		}

		String[] remaining = Arrays.copyOfRange(this.segments, i, this.segments.length);
		return new ResourcePath(remaining, this.dir);
	}

	@NotNull
	public ResourcePath join(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return this.join(of(path));
	}

	@NotNull
	public ResourcePath join(@NotNull String... path) {
		Objects.requireNonNull(path, "path is null");
		return this.join(of(path));
	}

	@NotNull
	public ResourcePath join(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		String[] allSegments = ArrayUtils.addAll(this.segments, path.segments);
		return new ResourcePath(allSegments, path.dir);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public boolean startsWith(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return this.startsWith(splitPath(path));
	}

	public boolean startsWith(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return this.startsWith(path.segments);
	}

	private boolean startsWith(@NotNull String[] segments) {
		Objects.requireNonNull(segments, "segments is null");

		if (segments.length > this.segments.length)
			return false;

		for (int i = 0; i < segments.length; i++) {
			if (!Objects.equals(this.segments[i], segments[i]))
				return false;
		}

		return true;
	}

	public boolean endsWith(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return endsWith(splitPath(path));
	}

	public boolean endsWith(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return endsWith(path.segments);
	}

	private boolean endsWith(@NotNull String[] segments) {
		Objects.requireNonNull(segments, "segments is null");

		if (segments.length > this.segments.length)
			return false;

		int offset = this.segments.length - segments.length;
		for (int i = 0; i < segments.length; i++) {
			if (!Objects.equals(this.segments[offset + i], segments[i]))
				return false;
		}

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public ResourcePath lower() {
		if (this.isEmpty())
			return this;

		String[] newSegments = this.segments.clone();
		for (int i = 0; i < newSegments.length; i++)
			newSegments[i] = newSegments[i].toLowerCase(Locale.ROOT);
		return new ResourcePath(newSegments, this.dir);
	}

	@NotNull
	public ResourcePath upper() {
		if (this.isEmpty())
			return this;

		String[] newSegments = this.segments.clone();
		for (int i = 0; i < newSegments.length; i++)
			newSegments[i] = newSegments[i].toUpperCase(Locale.ROOT);
		return new ResourcePath(newSegments, this.dir);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public ResourcePath withName(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");

		String[] newSegments = this.segments.clone();
		newSegments[newSegments.length - 1] = name;
		return new ResourcePath(newSegments, this.dir);
	}

	@NotNull
	public ResourcePath transformName(@NotNull UnaryOperator<String> transformer) {
		Objects.requireNonNull(transformer, "transformer is null");
		return this.withName(transformer.apply(this.getName()));
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public String[] toArray() {
		return this.segments.clone();
	}

	@NotNull
	public List<String> toList() {
		return List.of(this.segments);
	}

	@NotNull
	@Override
	public Iterator<String> iterator() {
		return new ArrayIterator<>(this.segments);
	}

	@Override
	public int compareTo(@NotNull ResourcePath other) {
		Objects.requireNonNull(other, "other is null");

		int len1 = this.segments.length;
		int len2 = other.segments.length;
		int min = Math.min(len1, len2);

		for (int i = 0; i < min; i++) {
			int cmp = this.segments[i].compareTo(other.segments[i]);
			if (cmp != 0)
				return cmp;
		}

		return Integer.compare(len1, len2);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ResourcePath strings = (ResourcePath) o;
		return this.dir == strings.dir
			&& Arrays.equals(this.segments, strings.segments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(this.segments), this.dir);
	}

	@Override
	public String toString() {
		if (this.isEmpty())
			return this.dir ? SEPARATOR : "";

		StringBuilder sb = new StringBuilder();

		for (String segment : this.segments) {
			sb.append(segment);
			sb.append(SEPARATOR_CHAR);
		}

		if (!this.dir)
			sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}
}
