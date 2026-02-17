package io.github.wsyong11.gameforge.framework;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class Identifier implements Comparable<Identifier> {
	public static final char NAMESPACE_SEPARATOR = ':';
	public static final String DEFAULT_NAMESPACE = "game";
	public static final Pattern MATCH_REGEX = Pattern.compile("^(?:([a-z0-9_.-]+):)?([a-zA-Z0-9_./-]+)$");

	@NotNull
	public static Identifier of(@NotNull String namespace, @NotNull String path) {
		Objects.requireNonNull(namespace, "namespace is null");
		Objects.requireNonNull(path, "path is null");

		if (!isValidNamespace(namespace))
			throw new IllegalArgumentException("Namespace \"" + namespace + "\" is not valid");

		if (!isValidPath(path))
			throw new IllegalArgumentException("Path \"" + path + "\" is not valid");

		return new Identifier(namespace, path);
	}

	@NotNull
	public static Identifier withDefaultNamespace(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");
		return of(DEFAULT_NAMESPACE, path);
	}

	@NotNull
	public static Identifier parse(@NotNull String identifier) {
		Objects.requireNonNull(identifier, "identifier is null");

		int index = identifier.indexOf(NAMESPACE_SEPARATOR);
		if (index <= 0)
			throw new IllegalArgumentException("Cannot parse identifier: \"" + identifier + "\", missing or invalid namespace separator");

		return of(
			identifier.substring(0, index),
			identifier.substring(index + 1)
		);
	}

	@NotNull
	public static Identifier parse(@NotNull String identifier, @NotNull String defaultNameSpace) {
		Objects.requireNonNull(identifier, "identifier is null");
		Objects.requireNonNull(defaultNameSpace, "defaultNameSpace is null");

		int index = identifier.indexOf(NAMESPACE_SEPARATOR);

		String namespace, path;
		if (index > 0) {
			namespace = identifier.substring(0, index);
			path = identifier.substring(index + 1);
		} else {
			namespace = defaultNameSpace;
			path = identifier;
		}

		return of(namespace, path);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static boolean isPathChar(char c) {
		return (c == '_')
			|| (c == '-')
			|| (c >= 'a' && c <= 'z')
			|| (c >= 'A' && c <= 'Z')
			|| (c >= '0' && c <= '9')
			|| (c == '/')
			|| (c == '.');
	}

	public static boolean isValidPath(@NotNull CharSequence path) {
		Objects.requireNonNull(path, "path is null");

		for (int i = 0; i < path.length(); ++i)
			if (!isPathChar(path.charAt(i)))
				return false;

		return true;
	}

	public static boolean isNamespaceChar(char c) {
		return (c == '_')
			|| (c == '-')
			|| (c >= 'a' && c <= 'z')
			|| (c >= '0' && c <= '9')
			|| (c == '.');
	}

	public static boolean isValidNamespace(@NotNull CharSequence namespace) {
		Objects.requireNonNull(namespace, "namespace is null");

		for (int i = 0; i < namespace.length(); ++i)
			if (!isNamespaceChar(namespace.charAt(i))) return false;

		return true;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final String namespace;
	private final String path;

	protected Identifier(@NotNull String namespace, @NotNull String path) {
		Objects.requireNonNull(namespace, "namespace is null");
		Objects.requireNonNull(path, "path is null");
		this.namespace = namespace;
		this.path = path;
	}

	@NotNull
	public String getNamespace() {
		return this.namespace;
	}

	@NotNull
	public String getPath() {
		return this.path;
	}

	@NotNull
	public Identifier transformPath(@NotNull UnaryOperator<String> operator) {
		Objects.requireNonNull(operator, "operator is null");
		return this.withPath(operator.apply(this.getPath()));
	}

	@NotNull
	public Identifier withPath(@NotNull String path) {
		Objects.requireNonNull(path, "path is null");

		if (path.equals(this.path))
			return this;

		return of(this.getNamespace(), path);
	}

	@Override
	public String toString() {
		return this.getNamespace() + NAMESPACE_SEPARATOR + this.getPath();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Identifier that = (Identifier) o;

		return Objects.equals(this.namespace, that.namespace)
			&& Objects.equals(this.path, that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.namespace, this.path);
	}

	@Override
	public int compareTo(@NotNull Identifier obj) {
		Objects.requireNonNull(obj, "obj is null");

		int i = this.getPath().compareTo(obj.getPath());
		if (i == 0) return this.getNamespace().compareTo(obj.getNamespace());

		return i;
	}
}
