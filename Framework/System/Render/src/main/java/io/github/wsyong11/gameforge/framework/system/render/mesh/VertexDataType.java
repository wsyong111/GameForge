package io.github.wsyong11.gameforge.framework.system.render.mesh;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class VertexDataType {
	// Float
	public static final VertexDataType FLOAT = new VertexDataType(BaseType.FLOAT, 1, false);
	public static final VertexDataType FLOAT2 = new VertexDataType(BaseType.FLOAT, 2, false);
	public static final VertexDataType FLOAT3 = new VertexDataType(BaseType.FLOAT, 3, false);
	public static final VertexDataType FLOAT4 = new VertexDataType(BaseType.FLOAT, 4, false);

	public static final VertexDataType H_FLOAT = new VertexDataType(BaseType.HALF_FLOAT, 1, false);
	public static final VertexDataType H_FLOAT2 = new VertexDataType(BaseType.HALF_FLOAT, 2, false);
	public static final VertexDataType H_FLOAT3 = new VertexDataType(BaseType.HALF_FLOAT, 3, false);
	public static final VertexDataType H_FLOAT4 = new VertexDataType(BaseType.HALF_FLOAT, 4, false);

	// Integer / Unsigned integer
	public static final VertexDataType BYTE = new VertexDataType(BaseType.BYTE, 1, false);
	public static final VertexDataType BYTE2 = new VertexDataType(BaseType.BYTE, 2, false);
	public static final VertexDataType BYTE3 = new VertexDataType(BaseType.BYTE, 3, false);
	public static final VertexDataType BYTE4 = new VertexDataType(BaseType.BYTE, 4, false);

	public static final VertexDataType U_BYTE = new VertexDataType(BaseType.UNSIGNED_BYTE, 1, false);
	public static final VertexDataType U_BYTE2 = new VertexDataType(BaseType.UNSIGNED_BYTE, 2, false);
	public static final VertexDataType U_BYTE3 = new VertexDataType(BaseType.UNSIGNED_BYTE, 3, false);
	public static final VertexDataType U_BYTE4 = new VertexDataType(BaseType.UNSIGNED_BYTE, 4, false);

	public static final VertexDataType SHORT = new VertexDataType(BaseType.SHORT, 1, false);
	public static final VertexDataType SHORT2 = new VertexDataType(BaseType.SHORT, 2, false);
	public static final VertexDataType SHORT3 = new VertexDataType(BaseType.SHORT, 3, false);
	public static final VertexDataType SHORT4 = new VertexDataType(BaseType.SHORT, 4, false);

	public static final VertexDataType U_SHORT = new VertexDataType(BaseType.UNSIGNED_SHORT, 1, false);
	public static final VertexDataType U_SHORT2 = new VertexDataType(BaseType.UNSIGNED_SHORT, 2, false);
	public static final VertexDataType U_SHORT3 = new VertexDataType(BaseType.UNSIGNED_SHORT, 3, false);
	public static final VertexDataType U_SHORT4 = new VertexDataType(BaseType.UNSIGNED_SHORT, 4, false);

	public static final VertexDataType INT = new VertexDataType(BaseType.INT, 1, false);
	public static final VertexDataType INT2 = new VertexDataType(BaseType.INT, 2, false);
	public static final VertexDataType INT3 = new VertexDataType(BaseType.INT, 3, false);
	public static final VertexDataType INT4 = new VertexDataType(BaseType.INT, 4, false);

	public static final VertexDataType U_INT = new VertexDataType(BaseType.UNSIGNED_INT, 1, false);
	public static final VertexDataType U_INT2 = new VertexDataType(BaseType.UNSIGNED_INT, 2, false);
	public static final VertexDataType U_INT3 = new VertexDataType(BaseType.UNSIGNED_INT, 3, false);
	public static final VertexDataType U_INT4 = new VertexDataType(BaseType.UNSIGNED_INT, 4, false);

	// Normalize type
	public static final VertexDataType BYTE_NORM = new VertexDataType(BaseType.BYTE, 1, true);
	public static final VertexDataType BYTE2_NORM = new VertexDataType(BaseType.BYTE, 2, true);
	public static final VertexDataType BYTE3_NORM = new VertexDataType(BaseType.BYTE, 3, true);
	public static final VertexDataType BYTE4_NORM = new VertexDataType(BaseType.BYTE, 4, true);

	public static final VertexDataType U_BYTE_NORM = new VertexDataType(BaseType.UNSIGNED_BYTE, 1, true);
	public static final VertexDataType U_BYTE2_NORM = new VertexDataType(BaseType.UNSIGNED_BYTE, 2, true);
	public static final VertexDataType U_BYTE3_NORM = new VertexDataType(BaseType.UNSIGNED_BYTE, 3, true);
	public static final VertexDataType U_BYTE4_NORM = new VertexDataType(BaseType.UNSIGNED_BYTE, 4, true);

	public static final VertexDataType SHORT_NORM = new VertexDataType(BaseType.SHORT, 1, true);
	public static final VertexDataType SHORT2_NORM = new VertexDataType(BaseType.SHORT, 2, true);
	public static final VertexDataType SHORT3_NORM = new VertexDataType(BaseType.SHORT, 3, true);
	public static final VertexDataType SHORT4_NORM = new VertexDataType(BaseType.SHORT, 4, true);

	public static final VertexDataType U_SHORT_NORM = new VertexDataType(BaseType.UNSIGNED_SHORT, 1, true);
	public static final VertexDataType U_SHORT2_NORM = new VertexDataType(BaseType.UNSIGNED_SHORT, 2, true);
	public static final VertexDataType U_SHORT3_NORM = new VertexDataType(BaseType.UNSIGNED_SHORT, 3, true);
	public static final VertexDataType U_SHORT4_NORM = new VertexDataType(BaseType.UNSIGNED_SHORT, 4, true);

	private final BaseType baseType;
	private final int components;
	private final boolean normalize;

	public VertexDataType(@NotNull BaseType baseType, int components, boolean normalize) {
		Objects.requireNonNull(baseType, "baseType is null");

		if (components < 1 || components > 4)
			throw new IllegalArgumentException("Component count only in range [1, 4]");

		this.baseType = baseType;
		this.components = components;
		this.normalize = normalize;
	}

	@NotNull
	public BaseType getBaseType() {
		return this.baseType;
	}

	public int getComponents() {
		return this.components;
	}

	public boolean isNormalize() {
		return this.normalize;
	}

	public int getByteSize() {
		return this.baseType.getByteSize() * this.components;
	}

	@Override
	public String toString() {
		return "Vertex[" + this.baseType + " * " + this.components + (this.normalize ? " normalize" : "") + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VertexDataType that = (VertexDataType) o;
		return this.components == that.components
			&& this.normalize == that.normalize
			&& this.baseType == that.baseType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.baseType, this.components, this.normalize);
	}

	public enum BaseType {
		FLOAT(Float.BYTES),
		HALF_FLOAT(Float.BYTES / 2),

		BYTE(Byte.BYTES),
		UNSIGNED_BYTE(Byte.BYTES),

		SHORT(Short.BYTES),
		UNSIGNED_SHORT(Short.BYTES),

		INT(Integer.BYTES),
		UNSIGNED_INT(Integer.BYTES);

		private final int byteSize;

		BaseType(int byteSize) {
			assert byteSize > 0;
			this.byteSize = byteSize;
		}

		public int getByteSize() {
			return this.byteSize;
		}

		public boolean isInt() {
			return this == BYTE
				|| this == UNSIGNED_BYTE
				|| this == SHORT
				|| this == UNSIGNED_SHORT
				|| this == INT
				|| this == UNSIGNED_INT;
		}

		public boolean isFloat() {
			return this == FLOAT
				|| this == HALF_FLOAT;
		}
	}
}
