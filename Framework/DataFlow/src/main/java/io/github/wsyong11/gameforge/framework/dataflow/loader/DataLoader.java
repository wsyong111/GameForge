package io.github.wsyong11.gameforge.framework.dataflow.loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wsyong11.gameforge.framework.dataflow.ex.ParserException;
import io.github.wsyong11.gameforge.framework.dataflow.ex.ParserNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class DataLoader<T> {
	@Nullable
	protected abstract Parser<T> getParser(int version);

	protected abstract int getLatestVersion();

	protected int getVersion(@NotNull JsonNode node) {
		Objects.requireNonNull(node, "node is null");

		JsonNode version = node.get("_version");
		return version.asInt(this.getLatestVersion());
	}

	@NotNull
	public T load(@NotNull InputStream stream, @NotNull ObjectMapper mapper) throws IOException {
		Objects.requireNonNull(stream, "stream is null");

		JsonNode node = mapper.readTree(stream);
		int version = this.getVersion(node);

		Parser<T> parser = this.getParser(version);
		if (parser == null)
			throw new ParserNotFoundException("Parser isn't found from the version " + version);

		return parser.parse(node);
	}

	public interface Parser<T> {
		@NotNull
		T parse(@NotNull JsonNode node) throws ParserException;
	}
}
