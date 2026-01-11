package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import io.github.wsyong11.gameforge.util.reflect.ReflectUtils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Objects;

public class TypeVariableToken {
	@NotNull
	public static TypeVariableToken of(@NotNull TypeVariable<?> variable) {
		Objects.requireNonNull(variable, "variable is null");
		return new TypeVariableToken(variable);
	}

	@NotNull
	public static TypeVariableToken of(@NotNull Class<?> type, @NotNull String name) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(name, "name is null");

		for (TypeVariable<? extends Class<?>> parameter : type.getTypeParameters()) {
			if (name.equals(parameter.getName()))
				return of(parameter);
		}

		throw new IllegalArgumentException("Cannot find type parameter " + name + " in class " + type.getName());
	}

	@NotNull
	public static TypeVariableToken of(@NotNull Class<?> type, int index) {
		Objects.requireNonNull(type, "type is null");

		TypeVariable<? extends Class<?>>[] parameters = type.getTypeParameters();
		Objects.checkIndex(index, parameters.length);

		return of(parameters[index]);
	}

	private final TypeVariable<?> variable;

	protected TypeVariableToken(@NotNull TypeVariable<?> variable) {
		Objects.requireNonNull(variable, "variable is null");
		this.variable = variable;
	}

	@NotNull
	public TypeVariable<?> getVariable() {
		return this.variable;
	}

	@NotNull
	public GenericDeclaration getDeclaration() {
		return this.variable.getGenericDeclaration();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TypeVariableToken that = (TypeVariableToken) o;
		return Objects.equals(this.variable, that.variable);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.variable);
	}

	@Override
	public String toString() {
		return "TypeVariableToken<" + ReflectUtils.declarationToString(this.getDeclaration()) + ">(" + this.variable.getName() + ")";
	}
}
