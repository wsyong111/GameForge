package io.github.wsyong11.gameforge.framework.system.window.icon;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

@UtilityClass
public class IconIO {
	@NotNull
	public static Icon read(@NotNull InputStream stream) throws IOException {
		Objects.requireNonNull(stream, "stream is null");

		byte[] data = stream.readAllBytes();
		ByteBuffer imageData = MemoryUtil.memAlloc(data.length);
		imageData.put(data);
		imageData.flip();

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer widthP = stack.mallocInt(1);
			IntBuffer heightP = stack.mallocInt(1);
			IntBuffer compP = stack.mallocInt(1);

			ByteBuffer pixels = stbi_load_from_memory(imageData,
				widthP,
				heightP,
				compP,
				4
			);

			if (pixels == null) {
				String reason = stbi_failure_reason();
				throw new IOException("STB cannot load image: " + reason);
			}

			Vector2i size = new Vector2i(
				widthP.get(),
				heightP.get()
			);

			return new STBSingleIcon(size, pixels);
		} finally {
			MemoryUtil.memFree(imageData);
		}
	}
}
