package io.github.wsyong11.gameforge.framework.dataflow.path;

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface ElementPath {
	static ElementPath compile(@NotNull @Language("JSONPath") String path) {
		Objects.requireNonNull(path, "path is null");
		return new JsonPathElementParser(path).parse();
	}

	@NotNull
	String toString();
}
