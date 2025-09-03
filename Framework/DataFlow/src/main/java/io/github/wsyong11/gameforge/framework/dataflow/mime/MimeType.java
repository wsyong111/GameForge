package io.github.wsyong11.gameforge.framework.dataflow.mime;

import io.github.wsyong11.gameforge.framework.dataflow.ex.MimeParseException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MimeType {
	private static final Map<String, MimeType> extensionMap = new ConcurrentHashMap<>();

	private static final MimeType DEFAULT = new MimeType("application", "octet-stream");

	public static void registerExtension(@NotNull String extension, @NotNull MimeType type) {
		Objects.requireNonNull(extension, "extension is null");
		Objects.requireNonNull(type, "type is null");

		String normalizedExtension = StringUtils.removeStart(extension, '.');
		extensionMap.put(normalizedExtension, type);
	}

	@NotNull
	public static MimeType getWithExtension(@NotNull String extension) {
		Objects.requireNonNull(extension, "extension is null");

		String normalizedExtension = StringUtils.removeStart(extension, '.');
		return extensionMap.getOrDefault(normalizedExtension, DEFAULT);
	}

	static {
		registerExtension("aac", new MimeType("audio", "aac"));
		registerExtension("abw", new MimeType("application", "x-abiword"));
		registerExtension("apng", new MimeType("image", "apng"));
		registerExtension("arc", new MimeType("application", "x-freearc"));
		registerExtension("avif", new MimeType("image", "avif"));
		registerExtension("avi", new MimeType("video", "x-msvideo"));
		registerExtension("azw", new MimeType("application", "vnd.amazon.ebook"));
		registerExtension("bin", new MimeType("application", "octet-stream"));
		registerExtension("bmp", new MimeType("image", "bmp"));
		registerExtension("bz", new MimeType("application", "x-bzip"));
		registerExtension("bz2", new MimeType("application", "x-bzip2"));
		registerExtension("cda", new MimeType("application", "x-cdf"));
		registerExtension("csh", new MimeType("application", "x-csh"));
		registerExtension("css", new MimeType("text", "css"));
		registerExtension("csv", new MimeType("text", "csv"));
		registerExtension("doc", new MimeType("application", "msword"));
		registerExtension("docx", new MimeType("application", "vnd.openxmlformats-officedocument.wordprocessingml.document"));
		registerExtension("eot", new MimeType("application", "vnd.ms-fontobject"));
		registerExtension("epub", new MimeType("application", "epub+zip"));
		registerExtension("gz", new MimeType("application", "gzip."));
		registerExtension("gif", new MimeType("image", "gif"));
		registerExtension("htm", new MimeType("text", "html"));
		registerExtension("html", new MimeType("text", "html"));
		registerExtension("ico", new MimeType("image", "vnd.microsoft.icon"));
		registerExtension("ics", new MimeType("text", "calendar"));
		registerExtension("jar", new MimeType("application", "java-archive"));
		registerExtension("jpeg", new MimeType("image", "jpeg"));
		registerExtension("jpg", new MimeType("image", "jpeg"));
		registerExtension("js", new MimeType("text", "javascript"));
		registerExtension("json", new MimeType("application", "json"));
		registerExtension("jsonld", new MimeType("application", "ld+json"));
		registerExtension("md", new MimeType("text", "markdown"));
		registerExtension("mid", new MimeType("audio", "midi"));
		registerExtension("midi", new MimeType("audio", "midi"));
		registerExtension("mjs", new MimeType("text", "javascript"));
		registerExtension("mp3", new MimeType("audio", "mpeg"));
		registerExtension("mp4", new MimeType("video", "mp4"));
		registerExtension("mpeg", new MimeType("video", "mpeg"));
		registerExtension("mpkg", new MimeType("application", "vnd.apple.installer+xml"));
		registerExtension("odp", new MimeType("application", "vnd.oasis.opendocument.presentation"));
		registerExtension("ods", new MimeType("application", "vnd.oasis.opendocument.spreadsheet"));
		registerExtension("odt", new MimeType("application", "vnd.oasis.opendocument.text"));
		registerExtension("oga", new MimeType("audio", "ogg"));
		registerExtension("ogv", new MimeType("video", "ogg"));
		registerExtension("ogx", new MimeType("application", "ogg"));
		registerExtension("opus", new MimeType("audio", "ogg"));
		registerExtension("otf", new MimeType("font", "otf"));
		registerExtension("png", new MimeType("image", "png"));
		registerExtension("pdf", new MimeType("application", "pdf"));
		registerExtension("php", new MimeType("application", "x-httpd-php"));
		registerExtension("ppt", new MimeType("application", "vnd.ms-powerpoint"));
		registerExtension("pptx", new MimeType("application", "vnd.openxmlformats-officedocument.presentationml.presentation"));
		registerExtension("rar", new MimeType("application", "vnd.rar"));
		registerExtension("rtf", new MimeType("application", "rtf"));
		registerExtension("sh", new MimeType("application", "x-sh"));
		registerExtension("svg", new MimeType("image", "svg+xml"));
		registerExtension("tar", new MimeType("application", "x-tar"));
		registerExtension("tif", new MimeType("image", "tiff"));
		registerExtension("tiff", new MimeType("image", "tiff"));
		registerExtension("ts", new MimeType("video", "mp2t"));
		registerExtension("ttf", new MimeType("font", "ttf"));
		registerExtension("txt", new MimeType("text", "plain"));
		registerExtension("vsd", new MimeType("application", "vnd.visio"));
		registerExtension("wav", new MimeType("audio", "wav"));
		registerExtension("weba", new MimeType("audio", "webm"));
		registerExtension("webm", new MimeType("video", "webm"));
		registerExtension("webmanifest", new MimeType("application", "manifest+json"));
		registerExtension("webp", new MimeType("image", "webp"));
		registerExtension("woff", new MimeType("font", "woff"));
		registerExtension("woff2", new MimeType("font", "woff2"));
		registerExtension("xhtml", new MimeType("application", "xhtml+xml"));
		registerExtension("xls", new MimeType("application", "vnd.ms-excel"));
		registerExtension("xlsx", new MimeType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		registerExtension("xml", new MimeType("application", "xml"));
		registerExtension("xul", new MimeType("application", "vnd.mozilla.xul+xml"));
		registerExtension("zip", new MimeType("application", "zip."));
		registerExtension("7z", new MimeType("application", "x-7z-compressed"));
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public static MimeType parse(@NotNull String mime) throws MimeParseException {
		Objects.requireNonNull(mime, "mime is null");

		int subtypeIndex = mime.indexOf('/');
		if (subtypeIndex <= 0) {
			throw new MimeParseException("Not a valid mime type");
		}

		String type = mime.substring(0, subtypeIndex);

		int parameterIndex = mime.indexOf(';');

		String subType;
		if (parameterIndex == -1) {
			subType = mime.substring(subtypeIndex + 1);
		} else {
			subType = mime.substring(subtypeIndex + 1, parameterIndex);
		}

		Map<String, String> parameters = new LinkedHashMap<>();
		if (parameterIndex != -1) {
			String paremeterString = mime.substring(parameterIndex + 1);
			for (String part : paremeterString.split(";")) {
				String trimPart = part.trim();
				if (trimPart.isEmpty()) {
					continue;
				}

				String[] partItems = trimPart.split("=", 2);
				if (partItems.length != 2) {
					throw new MimeParseException("Invalid parameter: " + trimPart);
				}

				String key = partItems[0].trim();
				String item = parseParameterValue(partItems[1].trim());
				parameters.put(key, item);
			}
		}

		return new MimeType(type, subType, parameters);
	}

	@NotNull
	private static String parseParameterValue(@NotNull String raw) {
		Objects.requireNonNull(raw, "raw is null");

		if (raw.isEmpty()) {
			return raw;
		}

		int length = raw.length();

		boolean startswithQuoted = raw.charAt(0) == '"';
		boolean endswithQuoted = raw.charAt(length - 1) == '"';
		if (length == 1 && startswithQuoted) {
			throw new MimeParseException("Invalid quoted string: ends with escape");
		}

		if (length == 2 && endswithQuoted) {
			return "";
		}

		if (startswithQuoted && endswithQuoted) {
			int textLength = length - 2;

			StringBuilder sb = new StringBuilder(textLength);

			boolean escape = false;

			int i = 1;
			while (i < textLength) {
				int c = raw.codePointAt(i);

				if (escape) {
					sb.appendCodePoint(c); // RFC: 保留任何 \"X
					escape = false;
				} else if (c == '\\') {
					escape = true;
				} else {
					sb.appendCodePoint(c);
				}

				i += Character.charCount(c);
			}

			if (escape) {
				throw new MimeParseException("Invalid quoted string: ends with escape");
			}

			return sb.toString();
		}

		return raw;
	}

	public static boolean isSafeParameter(char c) {
		return (c >= 'a' && c <= 'z')
			|| (c >= 'A' && c <= 'Z')
			|| (c >= '0' && c <= '9')
			|| c == '!' || c == '#' || c == '$'
			|| c == '%' || c == '&' || c == '\''
			|| c == '*' || c == '+' || c == '-'
			|| c == '.' || c == '^' || c == '_'
			|| c == '`' || c == '|' || c == '~';
	}

	public static boolean isSafeParameter(@NotNull String text) {
		Objects.requireNonNull(text, "text is null");

		for (int i = 0; i < text.length(); i++) {
			if (!isSafeParameter(text.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	@NotNull
	public static String toSafeParameter(@NotNull String text) {
		Objects.requireNonNull(text, "text is null");

		if (text.isEmpty()) {
			return "\"\"";
		}

		if (isSafeParameter(text)) {
			return text;
		}

		StringBuilder sb = new StringBuilder();
		sb.append('"');
		for (char c : text.toCharArray()) {
			if (c == '"' || c == '\\') {
				sb.append('\\');
			}
			sb.append(c);
		}
		sb.append('"');
		return sb.toString();
	}

	private final String type;
	private final String subtype;
	private final Map<String, String> parameters;

	public MimeType(@NotNull String type, @NotNull String subtype) {
		this(type, subtype, null);
	}

	public MimeType(@NotNull String type, @NotNull String subtype, @Nullable Map<String, String> parameters) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(subtype, "subtype is null");

		this.type = type.toLowerCase(Locale.ROOT);
		this.subtype = subtype.toLowerCase(Locale.ROOT);
		this.parameters = parameters == null ? Map.of() : new LinkedHashMap<>(parameters);
	}

	@NotNull
	public String getType() {
		return this.type;
	}

	@NotNull
	public String getSubtype() {
		return this.subtype;
	}

	public boolean hasParameter() {
		return !this.parameters.isEmpty();
	}

	@NotNull
	@Unmodifiable
	public Map<String, String> getParameters() {
		return Collections.unmodifiableMap(this.parameters);
	}

	@Nullable
	public String getParameter(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.parameters.get(key);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		MimeType mimeType = (MimeType) o;
		return Objects.equals(this.type, mimeType.type)
			&& Objects.equals(this.subtype, mimeType.subtype)
			&& Objects.equals(this.parameters, mimeType.parameters);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.type, this.subtype, this.parameters);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.type);
		sb.append('/');
		sb.append(this.subtype);

		for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
			sb.append("; ");
			sb.append(entry.getKey());
			sb.append('=');
			sb.append(toSafeParameter(entry.getValue()));
		}

		return sb.toString();
	}
}
