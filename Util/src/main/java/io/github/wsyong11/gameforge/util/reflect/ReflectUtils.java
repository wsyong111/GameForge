package io.github.wsyong11.gameforge.util.reflect;

import com.google.common.reflect.TypeToken;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
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

		return modifiers + " " + method.getDeclaringClass()
			.getName() + "#" + method.getName() + "(" + params + "): " + returnType;
	}

	@NotNull
	public static String declarationToString(@NotNull GenericDeclaration declaration) {
		Objects.requireNonNull(declaration, "declaration is null");

		if (declaration instanceof Class<?> cls)
			return cls.getName();

		if (declaration instanceof Method m)
			return m.getDeclaringClass().getName() + "#" + m.getName();

		if (declaration instanceof Constructor<?> c)
			return c.getDeclaringClass().getName() + "#<init>";

		return declaration.toString();
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
		} catch (ClassNotFoundException ignored) {
			return false;
		}
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public static Class<?> findCommonSuperclass(@NotNull List<Class<?>> classes) {
		Objects.requireNonNull(classes, "classes is null");

		if (classes.isEmpty())
			return Object.class;

		Class<?> candidate = classes.get(0);

		while (candidate != null) {
			boolean allMatch = true;

			for (Class<?> c : classes) {
				if (!candidate.isAssignableFrom(c)) {
					allMatch = false;
					break;
				}
			}

			if (allMatch)
				return candidate;

			candidate = candidate.getSuperclass();
		}

		return Object.class;
	}

	@NotNull
	public static Class<?> getRawType(@NotNull Type type) {
		Objects.requireNonNull(type, "type is null");

		if (type instanceof Class<?> classType)
			return classType;

		if (type instanceof ParameterizedType parameterizedType)
			return (Class<?>) parameterizedType.getRawType();

		else if (type instanceof GenericArrayType genericArrayType) {
			Type componentType = genericArrayType.getGenericComponentType();
			return Array.newInstance(getRawType(componentType), 0).getClass();
		}

		if (type instanceof TypeVariable<?> typeVariable)
			return getRawType(typeVariable.getBounds()[0]);

		if (type instanceof WildcardType wildcardType)
			return getRawType(wildcardType.getUpperBounds()[0]);

		throw new IllegalArgumentException("Unknown type: " + type);
	}

	public static int getInheritanceDistance(@NotNull Class<?> child, @NotNull Class<?> parent) {
		if (child == parent)
			return 0;

		if (!parent.isAssignableFrom(child))
			throw new IllegalArgumentException(parent.getName() + " is not assignable from " + child.getName());

		int distance = 0;
		Class<?> current = child;
		while (current != null) {
			if (current == parent)
				return distance;

			current = current.getSuperclass();
			distance++;
		}

		return Integer.MAX_VALUE;
	}
}
