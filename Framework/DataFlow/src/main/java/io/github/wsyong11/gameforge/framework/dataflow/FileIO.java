package io.github.wsyong11.gameforge.framework.dataflow;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

@UtilityClass
public class FileIO {
	@NotNull
	public static InputStream openInputStream(@NotNull Path path) throws IOException {
		Objects.requireNonNull(path, "path is null");
		return Files.newInputStream(path, StandardOpenOption.READ);
	}

	@NotNull
	public static OutputStream openOutputStream(@NotNull Path path) throws IOException {
		Objects.requireNonNull(path, "path is null");
		return Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
	}
}
