package io.github.wsyong11.gameforge.framework.dataflow;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wsyong11.gameforge.framework.dataflow.ex.ParserException;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

@UtilityClass
public class JsonIO {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper(new JsonFactory());

	@NotNull
	public static JsonNode parseJson(@NotNull String json) throws ParserException {
		Objects.requireNonNull(json, "json is null");

		try {
			return JSON_MAPPER.readTree(json);
		} catch (JsonProcessingException e) {
			throw new ParserException("Cannot parse json", e);
		}
	}

	@NotNull
	public static JsonNode parseJson(@NotNull InputStream stream) throws IOException {
		Objects.requireNonNull(stream, "stream is null");
		return JSON_MAPPER.readTree(stream);
	}

	@NotNull
	public String toJson(@NotNull JsonNode json) {
		Objects.requireNonNull(json, "json is null");

		try {
			return JSON_MAPPER.writeValueAsString(json);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Cannot serialize to json", e);
		}
	}

	public void toJson(@NotNull OutputStream stream, @NotNull JsonNode json) throws IOException {
		Objects.requireNonNull(stream, "stream is null");
		Objects.requireNonNull(json, "json is null");

		try {
			JSON_MAPPER.writeValue(stream, json);
		} catch (JsonProcessingException e) {
			throw new IOException("Cannot serialize to json", e);
		}
	}

	@NotNull
	public static ObjectMapper getJsonMapper() {
		return JSON_MAPPER;
	}
}
