package io.github.wsyong11.gameforge.framework.system.window.icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector2ic;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.stb.STBImage.stbi_image_free;

class STBSingleIcon implements Icon {
	private final Vector2ic size;

	private volatile ByteBuffer buffer;

	public STBSingleIcon(@NotNull Vector2ic size, @NotNull ByteBuffer icon) {
		Objects.requireNonNull(size, "size is null");
		Objects.requireNonNull(icon, "icon is null");

		this.size = size;
		this.buffer = icon;
	}

	@Override
	public boolean isClosed() {
		return this.buffer == null;
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<Vector2ic> getSizes() {
		return List.of(this.size);
	}

	@NotNull
	@Override
	public ByteBuffer getImage(int index) {
		if (this.isClosed())
			throw new IllegalStateException("This icon is closed");

		if (index != 0)
			throw new IndexOutOfBoundsException(index);

		return this.buffer.asReadOnlyBuffer();
	}

	@Override
	public void close() {
		if (this.buffer == null)
			return;

		stbi_image_free(this.buffer);

		this.buffer = null;
	}
}
