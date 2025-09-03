package io.github.wsyong11.gameforge.util.reflect;

import lombok.experimental.UtilityClass;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ReflectUtils {
	@Nullable
	public static Class<?> getCallerClass(@NotNull StackWalker stackWalker, @NotNull Class<?> blocklistClass) {
		Objects.requireNonNull(stackWalker, "stackWalker is null");
		Objects.requireNonNull(blocklistClass, "blocklistClass is null");

		return stackWalker.walk(stream -> stream
			.map(StackWalker.StackFrame::getDeclaringClass)
			.filter(cls -> cls != ReflectUtils.class)
			.filter(cls -> cls != blocklistClass)
			.findFirst()
			.orElse(null));
	}

	@Nullable
	public static Class<?> getCallerClass(@NotNull StackWalker stackWalker, @NotNull Set<Class<?>> blacklistClasses) {
		Objects.requireNonNull(stackWalker, "stackWalker is null");
		Objects.requireNonNull(blacklistClasses, "blacklistClasses is null");

		return stackWalker.walk(stream -> stream
			.map(StackWalker.StackFrame::getDeclaringClass)
			.filter(cls -> cls != ReflectUtils.class)
			.filter(cls -> !blacklistClasses.contains(cls))
			.findFirst()
			.orElse(null));
	}

	public static boolean hasModifier(@NotNull Member member, int modifiers) {
		Objects.requireNonNull(member, "member is null");
		return (member.getModifiers() & modifiers) == modifiers;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public static String methodToString(@NotNull Method method) {
		Objects.requireNonNull(method, "method is null");

		String modifiers = Modifier.toString(method.getModifiers());
		String returnType = method.getReturnType().getName();
		String params = Arrays.stream(method.getParameterTypes())
		                      .map(Class::getName)
		                      .collect(Collectors.joining(", "));

		return modifiers + " " + method.getDeclaringClass().getName() + "#" + method.getName() + "(" + params + "): " + returnType;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static boolean tryLoadClass(@Language("jvm-class-name") @NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return tryLoadClass(name, ClassLoader.getSystemClassLoader());
	}

	public static boolean tryLoadClass(@Language("jvm-class-name") @NotNull String name, @NotNull ClassLoader classLoader) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(classLoader, "classLoader is null");

		try {
			classLoader.loadClass(name);
			return true;
		}catch (ClassNotFoundException ignored){
			return false;
		}
	}
}
