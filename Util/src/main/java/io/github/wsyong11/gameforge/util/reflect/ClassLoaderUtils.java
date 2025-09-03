package io.github.wsyong11.gameforge.util.reflect;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class ClassLoaderUtils {
	@NotNull
	public static String getName(@Nullable ClassLoader classLoader) {
		if (classLoader == null)
			return "bootstrap";

		String name = classLoader.getName();
		return name == null || name.isEmpty()
			? classLoader.getClass().getSimpleName() + "@" + System.identityHashCode(classLoader)
			: name;
	}
}
