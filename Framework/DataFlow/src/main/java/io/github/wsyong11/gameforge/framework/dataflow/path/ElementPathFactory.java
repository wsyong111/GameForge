package io.github.wsyong11.gameforge.framework.dataflow.path;

import io.github.wsyong11.gameforge.framework.dataflow.path.json.JsonPathElementParser;
import io.github.wsyong11.gameforge.framework.dataflow.path.json.JsonPathMode;
import lombok.experimental.UtilityClass;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class ElementPathFactory {
	@NotNull
	public static ElementPath compileJsonPath(@NotNull @Language("JSONPath") String path) {
		Objects.requireNonNull(path, "path is null");
		return compileJsonPath(path, JsonPathMode.RFC_9535);
	}

	@NotNull
	public static ElementPath compileJsonPath(@NotNull @Language("JSONPath") String path, @NotNull JsonPathMode mode) {
		Objects.requireNonNull(path, "path is null");
		Objects.requireNonNull(mode, "mode is null");
		return new JsonPathElementParser(path, mode).parse();
	}
}
