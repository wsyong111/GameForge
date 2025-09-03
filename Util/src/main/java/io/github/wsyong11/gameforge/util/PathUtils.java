package io.github.wsyong11.gameforge.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Objects;

@UtilityClass
public class PathUtils {
	public static String getExtension(@NotNull Path path){
		Objects.requireNonNull(path, "path is null");

		String fileName = path.getFileName().toString();
		int index = fileName.lastIndexOf('.');
		if (index<0)
			return "";
		return fileName.substring(index);
	}
}
