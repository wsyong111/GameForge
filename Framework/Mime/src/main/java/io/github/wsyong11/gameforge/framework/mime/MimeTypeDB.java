package io.github.wsyong11.gameforge.framework.mime;

import com.google.gson.*;
import io.github.wsyong11.gameforge.framework.ex.SyntaxException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MimeTypeDB {
	private static final Logger LOGGER = Log.getLogger();

	private static final String DATA_BASE = "mime-db.json";

	private static Map<String, MimeType> EXTENSION_MIME_MAP;

	private static void init() {
		ClassLoader classLoader = MimeTypeDB.class.getClassLoader();

		Map<String, MimeType> castMap = new HashMap<>();

		try (InputStream stream = classLoader.getResourceAsStream(DATA_BASE)) {
			if (stream == null)
				throw new FileNotFoundException(DATA_BASE);

			Gson gson = new Gson();

			JsonObject json = gson.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonObject.class);

			for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
				String key = entry.getKey();
				JsonElement value = entry.getValue();

				if (!(value instanceof JsonObject mimeInfoObject)) {
					LOGGER.warn("Invalid JSON type {}, request JsonObject, at key \"{}\"", value.getClass().getName(), key);
					continue;
				}

				if (!mimeInfoObject.has("extensions"))
					continue;

				JsonElement extensionsElement = mimeInfoObject.get("extensions");
				if (!(extensionsElement instanceof JsonArray extensionArray)) {
					LOGGER.warn("Invalid JSON type {}, request JsonArray, at key \"{}\".extensions", extensionsElement.getClass().getName(), key);
					continue;
				}

				MimeType mimeType;
				try {
					mimeType = MimeType.parse(key);
				} catch (SyntaxException e) {
					LOGGER.warn("Cannot parse mime \"{}\"", key, e);
					continue;
				}


				for (int i = 0; i < extensionArray.size(); i++) {
					JsonElement extensionItem = extensionArray.get(i);
					if (!(extensionItem instanceof JsonPrimitive primitive) || !primitive.isString()) {
						LOGGER.warn("Invalid JSON type {}, request JsonString, at key \"{}\".extensions[{}]", extensionItem.getClass().getName(), key, i);
						continue;
					}

					String extension = primitive.getAsString();

					if (castMap.containsKey(extension)) {
						LOGGER.verbose("Found a repeat extensions \"{}\" -> \"{}\", skipped", extension, key);
						continue;
					}
					castMap.put(extension, mimeType);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load the mime type database \"{}\"", DATA_BASE, e);
		}

		EXTENSION_MIME_MAP = Collections.unmodifiableMap(castMap);
	}

	static {
		init();
	}

	@NotNull
	public static MimeType get(@NotNull String extension) {
		Objects.requireNonNull(extension, "extension is null");

		String normalizeExtension = (extension.startsWith(".") ? extension.substring(1) : extension).toLowerCase(Locale.ROOT);
		return EXTENSION_MIME_MAP.getOrDefault(normalizeExtension, MimeTypes.Application.OCTET_STREAM);
	}
}
