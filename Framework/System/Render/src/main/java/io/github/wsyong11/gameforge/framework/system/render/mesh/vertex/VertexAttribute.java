package io.github.wsyong11.gameforge.framework.system.render.mesh.vertex;

import io.github.wsyong11.gameforge.framework.annotation.nio.DirectBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

public interface VertexAttribute {
	@NotNull
	String getName();

	@NotNull
	VertexDataType getType();

	@NotNull
	@UnmodifiableView
	@DirectBuffer
	ByteBuffer getBuffer();

	@SuppressWarnings("unchecked")
	@NotNull
	@UnmodifiableView
	@DirectBuffer
	default <T extends Buffer> T getBuffer(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		VertexDataType.BaseType baseType = this.getType().getBaseType();
		if (type == ByteBuffer.class || type == Buffer.class) {
			return (T) this.getBuffer();
		} else if (type == IntBuffer.class) {
			if (!baseType.isInt())
				throw new IllegalArgumentException("Cannot cast buffer to IntBuffer, baseType=" + baseType);

			return (T) this.getBuffer().asIntBuffer();
		} else if (type == FloatBuffer.class) {
			if (!baseType.isFloat())
				throw new IllegalArgumentException("Cannot cast buffer to FloatBuffer, baseType=" + baseType);
			return (T) this.getBuffer().asFloatBuffer();
		}

		throw new UnsupportedOperationException("Unsupported buffer type " + type.getName());
	}
}
