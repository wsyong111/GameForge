package io.github.wsyong11.gameforge.framework.system.render.shader;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ShaderDataType {
	// ===== Scalar Types =====
	public static final ShaderDataType FLOAT = ShaderDataType.baseType("float", ShaderDataType.BaseType.FLOAT, 1, Float.BYTES);
	public static final ShaderDataType INT = ShaderDataType.baseType("int", ShaderDataType.BaseType.INT, 1, Integer.BYTES);
	public static final ShaderDataType BOOL = ShaderDataType.baseType("bool", ShaderDataType.BaseType.BOOL, 1, 1);

	// ===== Vector Types =====
	public static final ShaderDataType VEC2 = ShaderDataType.baseType("vec2", ShaderDataType.BaseType.FLOAT, 2, Float.BYTES);
	public static final ShaderDataType VEC3 = ShaderDataType.baseType("vec3", ShaderDataType.BaseType.FLOAT, 3, Float.BYTES);
	public static final ShaderDataType VEC4 = ShaderDataType.baseType("vec4", ShaderDataType.BaseType.FLOAT, 4, Float.BYTES);

	public static final ShaderDataType IVEC2 = ShaderDataType.baseType("ivec2", ShaderDataType.BaseType.INT, 2, Integer.BYTES);
	public static final ShaderDataType IVEC3 = ShaderDataType.baseType("ivec3", ShaderDataType.BaseType.INT, 3, Integer.BYTES);
	public static final ShaderDataType IVEC4 = ShaderDataType.baseType("ivec4", ShaderDataType.BaseType.INT, 4, Integer.BYTES);

	public static final ShaderDataType BVEC2 = ShaderDataType.baseType("bvec2", ShaderDataType.BaseType.BOOL, 2, 1);
	public static final ShaderDataType BVEC3 = ShaderDataType.baseType("bvec3", ShaderDataType.BaseType.BOOL, 3, 1);
	public static final ShaderDataType BVEC4 = ShaderDataType.baseType("bvec4", ShaderDataType.BaseType.BOOL, 4, 1);

	// ===== Matrix Types =====
	public static final ShaderDataType MAT2 = ShaderDataType.floatMatrix(2, 2);
	public static final ShaderDataType MAT3 = ShaderDataType.floatMatrix(3, 3);
	public static final ShaderDataType MAT4 = ShaderDataType.floatMatrix(4, 4);
	public static final ShaderDataType MAT3x4 = ShaderDataType.floatMatrix(3, 4);

	// ===== Sampler Types =====
	public static final ShaderDataType SAMPLER2D = ShaderDataType.sampler("sampler2D");
	public static final ShaderDataType SAMPLER3D = ShaderDataType.sampler("sampler3D");
	public static final ShaderDataType SAMPLERCUBE = ShaderDataType.sampler("samplerCube");

	@NotNull
    public static ShaderDataType array(@NotNull ShaderDataType type, int length) {
		Objects.requireNonNull(type, "type is null");

		return new ShaderDataType(
            type.getName(),
            type.getBaseType(),
            type.getRows(),
            type.getCols(),
            type.getComponentSize(),
            length
        );
    }

	@NotNull
	public static ShaderDataType baseType(@NotNull String name, @NotNull BaseType baseType, int componentCount, int componentSize) {
		return new ShaderDataType(name, baseType, componentCount, 0, componentSize, 0);
	}

	@NotNull
	public static ShaderDataType floatMatrix(int rows, int cols) {
		return new ShaderDataType("mat" + rows + "x" + cols, BaseType.FLOAT, rows, cols, Float.BYTES, 0);
	}

	@NotNull
	public static ShaderDataType sampler(@NotNull String name) {
		return new ShaderDataType(name, BaseType.SAMPLER, 0, 0, 0, 0);
	}

	private final String name;
	private final BaseType baseType;
	private final int rows;
	private final int cols;
	private final int componentSize;
	private final int arrayLength;

	public ShaderDataType(@NotNull String name, @NotNull BaseType baseType, int rows, int cols, int componentSize, int arrayLength) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(baseType, "baseType is null");

		if (rows < 0)
			throw new IllegalArgumentException("Rows cannot be zero or negative");

		if (componentSize <= 0)
			throw new IllegalArgumentException("Component size cannot be zero or negative");

		this.name = name;
		this.baseType = baseType;
		this.rows = rows;
		this.cols = Math.max(0, cols);
		this.componentSize = componentSize;
		this.arrayLength = Math.max(0, arrayLength);
	}

	@NotNull
	public String getName() {
		return this.name;
	}

	@NotNull
	public BaseType getBaseType() {
		return this.baseType;
	}

	public int getRows() {
		return this.rows;
	}

	public int getCols() {
		return this.cols;
	}

	public int getComponentSize() {
		return this.componentSize;
	}

	public int getComponentCount() {
		return this.rows * Math.max(1, this.cols);
	}

	public int getArrayLength() {
		return this.arrayLength;
	}

	public int getSize() {
		return this.rows * Math.max(1, this.cols) * this.componentSize * Math.max(1, this.arrayLength);
	}

	public boolean isArray() {
		return this.arrayLength > 0;
	}

	public boolean isMatrix() {
		return this.cols > 0;
	}

	@Override
	public String toString() {
		return "%s{%s %s / %d bytes}%s".formatted(
			this.name,
			this.baseType,
			this.isMatrix() ? (this.rows + "x" + this.cols) : String.valueOf(this.rows),
			this.componentSize,
			this.isArray() ? "[" + this.arrayLength + "]" : ""
		);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShaderDataType that = (ShaderDataType) o;
		return this.rows == that.rows
			&& this.cols == that.cols
			&& this.componentSize == that.componentSize
			&& this.arrayLength == that.arrayLength
			&& this.baseType == that.baseType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.baseType, this.rows, this.cols, this.componentSize, this.arrayLength);
	}

	public enum BaseType {
		FLOAT,
		INT,
		BOOL,
		SAMPLER
	}
}
