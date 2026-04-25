package io.github.wsyong11.gameforge.framework.system.resource.v2;

import org.apache.commons.collections4.iterators.ArrayIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class ResourcePath implements Iterable<String>, Comparable<ResourcePath> {
	public static final String SEPARATOR = "/";
	public static final char SEPARATOR_CHAR = '/';

	public static final ResourcePath ROOT = new ResourcePath(ArrayUtils.EMPTY_STRING_ARRAY, true, true);
	public static final ResourcePath EMPTY = new ResourcePath(ArrayUtils.EMPTY_STRING_ARRAY, false, false);

	@NotNull
	public static ResourcePath of(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");

		int length = path.length();
		if (length == 0)
			return EMPTY;

		boolean absolute = isSeparator(path.charAt(0));
		if (length == 1 && absolute)
			return ROOT;

		boolean dir = isSeparator(path.charAt(length - 1));

		String[] segments = splitPath(path);
		return new ResourcePath(segments, dir, absolute);
	}

	@NotNull
	public static ResourcePath of(String... paths) {
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

			segments[segmentIndex++] = path.substring(start, i);
		}

		return segments;
	}

	@NotNull
	private static String[] normalize(@NotNull String[] segments) {
		Objects.requireNonNull(segments, "segments is null");

		String[] tmp = new String[segments.length];
		int size = 0;

		for (String seg : segments) {
			if (seg.isEmpty() || ".".equals(seg))
				continue;

			if ("..".equals(seg)) {
				if (size > 0)
					size--;
				continue;
			}

			tmp[size++] = seg;
		}

		return Arrays.copyOf(tmp, size);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final String[] segments;
	private final boolean dir;
	private final boolean absolute;

	protected ResourcePath(@NotNull String[] segments, boolean dir, boolean absolute) {
		Objects.requireNonNull(segments, "segments is null");

		this.segments = segments;
		this.dir = dir;
		this.absolute = absolute;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public boolean isDirectory() {
		return this.dir;
	}

	public boolean isAbsolute() {
		return this.absolute;
	}

	@NotNull
	public ResourcePath toDirectory() {
		return this.dir ? this : new ResourcePath(this.segments, true, this.absolute);

	}

	@NotNull
	public ResourcePath toFile() {
		return !this.dir ? this : new ResourcePath(this.segments, false, this.absolute);
	}

	@NotNull
	public ResourcePath toAbsolute(@NotNull ResourcePath base) {
		Objects.requireNonNull(base, "base is null");

		if (this.absolute)
			return this;

		if (base.isEmpty())
			return new ResourcePath(this.segments, this.dir, true);

		String[] newSegment = ArrayUtils.addAll(base.segments, this.segments);
		String[] normalized = normalize(newSegment);
		return new ResourcePath(normalized, this.dir, true);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public boolean isRoot() {
		return this == ROOT
			|| (this.isEmpty() && this.dir && this.absolute);
	}

	public int length() {
		return this.segments.length;
	}

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
		if (this.isEmpty() || this.segments.length == 1)
			return ROOT;

		String[] newSegment = Arrays.copyOf(this.segments, this.segments.length - 1);
		return new ResourcePath(newSegment, true, this.absolute);
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
			throw new ArrayIndexOutOfBoundsException(
				"Invalid sub path range: " + begin + " to " + end +
					", length=" + this.segments.length
			);

		if (begin == 0 && end == this.segments.length)
			return this;

		String[] sub = Arrays.copyOfRange(this.segments, begin, end);

		boolean isDir = this.dir || (end < this.segments.length);
		return new ResourcePath(sub, isDir, this.absolute);
	}

	public boolean isSubPathOf(@NotNull ResourcePath other) {
		Objects.requireNonNull(other, "other is null");
		return other.endsWith(this);
	}

	public boolean isSiblingOf(@NotNull ResourcePath other) {
		Objects.requireNonNull(other, "other is null");
		return other.startsWith(this);
	}

	@Contract("_, true -> !null")
	@Nullable
	public ResourcePath relativize(@NotNull ResourcePath base, boolean strict) {
		Objects.requireNonNull(base, "base is null");

		if (this.absolute != base.absolute) {
			if (strict)
				throw new IllegalArgumentException("Cannot relativize between absolute and relative paths");
			return null;
		}

		int max = Math.min(this.segments.length, base.segments.length);
		int i = 0;

		while (i < max && Objects.equals(this.segments[i], base.segments[i]))
			i++;

		int upCount = base.segments.length - i;

		String[] result = new String[upCount + (this.segments.length - i)];
		int index = 0;

		for (int j = 0; j < upCount; j++)
			result[index++] = "..";

		for (int j = i; j < this.segments.length; j++)
			result[index++] = this.segments[j];

		return new ResourcePath(result, this.dir, false);
	}

	@NotNull
	public ResourcePath resolve(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return this.resolve(of(path));
	}

	@NotNull
	public ResourcePath resolve(@NotNull String... path) {
		Objects.requireNonNull(path, "path is null");
		return this.resolve(of(path));
	}

	@NotNull
	public ResourcePath resolve(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		String[] allSegments = ArrayUtils.addAll(this.segments, path.segments);
		return new ResourcePath(allSegments, path.dir, this.absolute);
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
		return new ResourcePath(newSegments, this.dir, this.absolute);
	}

	@NotNull
	public ResourcePath upper() {
		if (this.isEmpty())
			return this;

		String[] newSegments = this.segments.clone();
		for (int i = 0; i < newSegments.length; i++)
			newSegments[i] = newSegments[i].toUpperCase(Locale.ROOT);
		return new ResourcePath(newSegments, this.dir, this.absolute);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public ResourcePath withName(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");

		String[] newSegments = this.segments.clone();
		newSegments[newSegments.length - 1] = name;
		return new ResourcePath(newSegments, this.dir, this.absolute);
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
		return dir == strings.dir
			&& absolute == strings.absolute
			&& Arrays.equals(this.segments, strings.segments);
	}

	@Override
	public int hashCode() {
		return Objects.hash(Arrays.hashCode(this.segments), this.dir, this.absolute);
	}

	@Override
	public String toString() {
		if (this.isEmpty())
			return this.dir ? SEPARATOR : "";

		StringBuilder sb = new StringBuilder();
		if (this.absolute)
			sb.append(SEPARATOR_CHAR);

		for (String segment : this.segments) {
			sb.append(segment);
			sb.append(SEPARATOR_CHAR);
		}

		if (!this.dir)
			sb.deleteCharAt(sb.length() - 1);

		return sb.toString();
	}
}
