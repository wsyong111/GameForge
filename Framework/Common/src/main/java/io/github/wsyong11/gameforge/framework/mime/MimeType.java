package io.github.wsyong11.gameforge.framework.mime;

import io.github.wsyong11.gameforge.framework.ex.SyntaxException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class MimeType implements Comparable<MimeType> {
	public static final String ANY_TYPE = "*";

	public static final MimeType ANY = new MimeType(ANY_TYPE, ANY_TYPE, Map.of());

	@NotNull
	public static MimeType parse(@NotNull String mime) {
		Objects.requireNonNull(mime, "mime is null");

		int typeSplitIndex = mime.indexOf('/');
		if (typeSplitIndex == -1)
			throw new SyntaxException("Missing subtype", mime.length(), mime);

		int parameterStartIndex = mime.indexOf(';', typeSplitIndex);
		if (parameterStartIndex == -1) {
			String type = mime.substring(0, typeSplitIndex).trim();
			String subtype = mime.substring(typeSplitIndex + 1).trim();

			if (!isWildcard(type) && !isValidToken(type))
				throw new SyntaxException("Type is not a valid token", 0, typeSplitIndex, mime);

			if (!isWildcard(subtype) && !isValidToken(subtype))
				throw new SyntaxException("Subtype is not a valid token", typeSplitIndex + 1, mime);

			return of(type.trim(), subtype.trim());
		}

		String type = mime.substring(0, typeSplitIndex);
		String subtype = mime.substring(typeSplitIndex + 1, parameterStartIndex);

		Map<String, String> parameter = parseParameter(mime, parameterStartIndex + 1);

		return of(type.trim(), subtype.trim(), parameter);
	}

	@NotNull
	private static Map<String, String> parseParameter(@NotNull String mime, int startIndex) {
		Objects.requireNonNull(mime, "mime is null");

		Map<String, String> parameters = new LinkedHashMap<>();

		int length = mime.length();

		int index = startIndex;
		while (index < length) {
			int ch = mime.codePointAt(index);

			if (Character.isWhitespace(ch)) {
				index += Character.charCount(ch);
				continue;
			}

			int splitIndex = mime.indexOf('=', index);
			if (splitIndex == -1)
				throw new SyntaxException("Parameter incomplete", length, mime);
			String rawKey = mime.substring(index, splitIndex);
			String key = rawKey.trim();

			if (key.isEmpty())
				throw new SyntaxException("Parameter key is empty", index - 1, mime);

			index += rawKey.length();
			index += 1; // '='

			while (index < length) {
				int c = mime.codePointAt(index);
				if (!Character.isWhitespace(c))
					break;

				index += Character.charCount(c);
			}

			if (index >= length)
				throw new SyntaxException("Parameter incomplete", index, mime);

			boolean quotesMode = mime.codePointAt(index) == '"';
			if (quotesMode)
				index++;

			int valueStartIndex = index;
			while (index < length) {
				int c = mime.codePointAt(index);

				if (c == '\\') {
					index += 2;
					continue;
				}

				if (quotesMode && c == '"')
					break;
				else if (!quotesMode && c == ';')
					break;

				index += Character.charCount(c);

				if (quotesMode && index >= length)
					throw new SyntaxException("Quotes are not closed", index, mime);
			}
			String rawValue = mime.substring(valueStartIndex, index);

			if (quotesMode)
				index++; // '"'

			String value;
			if (quotesMode) {
				value = rawValue
					.replace("\\\"", "\"")
					.replace("\\\\", "\\");
			} else {
				String trimValue = rawValue.trim();
				if (!isValidToken(trimValue))
					throw new SyntaxException("Parameter value is not a valid token", valueStartIndex, index - valueStartIndex, mime);

				value = trimValue;
			}

			parameters.put(key, value);

			if (index + 1 < length && mime.charAt(index) == ';')
				index++; // ';'
		}

		return parameters;
	}

	public static boolean isTokenChar(char c) {
		return (c >= 'a' && c <= 'z')
			|| (c >= 'A' && c <= 'Z')
			|| (c >= '0' && c <= '9')
			|| (c == '!')
			|| (c == '#')
			|| (c == '$')
			|| (c == '&')
			|| (c == '-')
			|| (c == '^')
			|| (c == '_')
			|| (c == '.')
			|| (c == '+');
	}

	public static boolean isValidToken(@NotNull CharSequence type) {
		Objects.requireNonNull(type, "type is null");

		int length = type.length();
		if (length == 0)
			return false;

		for (int i = 0; i < length; ++i)
			if (!isTokenChar(type.charAt(i)))
				return false;

		return true;
	}

	public static boolean isWildcard(@NotNull CharSequence cs) {
		Objects.requireNonNull(cs, "cs is null");
		return cs.length() == 1 && cs.charAt(0) == '*';
	}

	@NotNull
	public static MimeType application(@NotNull String subtype) {
		Objects.requireNonNull(subtype, "subtype is null");
		return of("application", subtype);
	}

	@NotNull
	public static MimeType of(@NotNull String type, @NotNull String subtype) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(subtype, "subtype is null");
		return of(type, subtype, Map.of());
	}

	@NotNull
	public static MimeType of(@NotNull String type, @NotNull String subtype, @NotNull Map<String, String> parameters) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(subtype, "subtype is null");

		boolean typeWildcard = isWildcard(type);
		boolean subtypeWildcard = isWildcard(subtype);

		if (typeWildcard && subtypeWildcard && parameters.isEmpty())
			return ANY;

		if (!typeWildcard && !isValidToken(type))
			throw new IllegalArgumentException("Type \"" + type + "\" is not valid");

		if (!subtypeWildcard && !isValidToken(subtype))
			throw new IllegalArgumentException("Subtype \"" + subtype + "\" is not valid");

		for (String key : parameters.keySet())
			if (!isValidToken(key))
				throw new IllegalArgumentException("Parameter key \"" + key + "\" is not valid");

		return new MimeType(type, subtype, parameters);
	}

	private final String type;
	private final String subtype;
	private final Map<String, String> parameters;

	protected MimeType(@NotNull String type, @NotNull String subtype, @NotNull Map<String, String> parameters) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(subtype, "subtype is null");
		Objects.requireNonNull(parameters, "parameters is null");

		this.type = type.toLowerCase(Locale.ROOT);
		this.subtype = subtype.toLowerCase(Locale.ROOT);

		Map<String, String> processedParameters = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			processedParameters.put(
				key.toLowerCase(Locale.ROOT),
				value
			);
		}

		this.parameters = Collections.unmodifiableMap(processedParameters);
	}

	@NotNull
	public String getType() {
		return this.type;
	}

	public boolean isWildcardType() {
		return isWildcard(this.type);
	}

	@NotNull
	public String getSubtype() {
		return this.subtype;
	}

	public boolean isWildcardSubtype() {
		return isWildcard(this.subtype);
	}

	public boolean isAny() {
		return this == ANY || (this.isWildcardType() && this.isWildcardSubtype());
	}

	public boolean isConcrete() {
		return !this.isWildcardType() && !this.isWildcardSubtype();
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@UnmodifiableView
	public Map<String, String> getParameters() {
		return this.parameters;
	}

	@Nullable
	public String getParameter(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.parameters.get(key.toLowerCase(Locale.ROOT));
	}

	@Contract("_, _ -> param2")
	@Nullable
	public String getParameter(@NotNull String key, @Nullable String defaultValue) {
		Objects.requireNonNull(key, "key is null");
		return Objects.requireNonNullElse(this.getParameter(key), defaultValue);
	}

	public boolean hasParameter(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.parameters.containsKey(key.toLowerCase(Locale.ROOT));
	}

	public int getParameterCount() {
		return this.parameters.size();
	}

	@NotNull
	public MimeType withParameter(@NotNull String key, @NotNull String value) {
		Objects.requireNonNull(key, "key is null");
		Objects.requireNonNull(value, "value is null");

		Map<String, String> newParams = new LinkedHashMap<>(this.parameters);
		newParams.put(key.toLowerCase(Locale.ROOT), value);

		return MimeType.of(this.type, this.subtype, newParams);
	}

	@NotNull
	public MimeType withoutParameter(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");

		String keyLowerCase = key.toLowerCase(Locale.ROOT);
		if (!this.parameters.containsKey(keyLowerCase))
			return this;

		Map<String, String> newParams = new LinkedHashMap<>(this.parameters);
		newParams.remove(keyLowerCase);

		return MimeType.of(this.type, this.subtype, newParams);
	}

	@NotNull
	public MimeType withoutParameters() {
		return of(this.type, this.subtype);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public boolean matches(@NotNull String mime) {
		Objects.requireNonNull(mime, "mime is null");
		return this.includes(parse(mime));
	}

	public boolean includes(@NotNull MimeType other) {
		Objects.requireNonNull(other, "other is null");

		return (this.isWildcardType() || this.type.equals(other.getType()))
			&& (this.isWildcardSubtype() || this.subtype.equals(other.getSubtype()));
	}

	public boolean compatible(@NotNull MimeType other) {
		Objects.requireNonNull(other, "other is null");

		return (this.isWildcardType() || other.isWildcardType() || this.type.equals(other.getType()))
			&& (this.isWildcardSubtype() || other.isWildcardSubtype() || this.subtype.equals(other.getSubtype()));
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public int getSpecificityScore() {
		int score = 0;

		if (!this.isWildcardType())
			score += 100;

		if (!this.isWildcardSubtype())
			score += 10;

		score += this.getParameterCount();

		return score;
	}

	@Override
	public int compareTo(@NotNull MimeType o) {
		Objects.requireNonNull(o, "o is null");

		if (this.isWildcardType() != o.isWildcardType())
			return this.isWildcardType() ? 1 : -1;

		if (this.isWildcardSubtype() != o.isWildcardSubtype())
			return this.isWildcardSubtype() ? 1 : -1;

		return Integer.compare(o.getParameterCount(), this.getParameterCount());
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MimeType that = (MimeType) o;
		return Objects.equals(this.type, that.type)
			&& Objects.equals(this.subtype, that.subtype)
			&& Objects.equals(this.parameters, that.parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.subtype, this.parameters);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.type)
			.append('/')
			.append(this.subtype);

		for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
			sb.append("; ");
			sb.append(entry.getKey());

			sb.append('=');

			String value = entry.getValue();
			if (isValidToken(value))
				sb.append(value);
			else
				sb.append('"')
					.append(value
						.replace("\\", "\\\\")
						.replace("\"", "\\\""))
					.append('"');
		}

		return sb.toString();
	}
}
