package io.github.wsyong11.gameforge.framework.system.graphic.surface.window;

import io.github.wsyong11.gameforge.framework.annotation.nio.DirectBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.List;

public interface Icon extends Closeable {
	boolean isClosed();

	@NotNull
	@Unmodifiable
	List<Vector2ic> getSizes();

	@NotNull
	ByteBuffer getImage(int index);

	@Override
	void close();

	interface Image {
		@NotNull
		Vector2ic getSize();

		void getSize(@NotNull Vector2i dest);

		/**
		 * 图像像素数据。返回的 {@link ByteBuffer} 为 RGBA8 格式，row-major 排列
		 *
		 * @return 只读像素缓冲区，position为0，limit和capacity都为数据长度，类型为直接缓冲区
		 * @throws IllegalStateException 在图标已关闭时抛出
		 */
		@NotNull
		@UnmodifiableView
		@DirectBuffer
		ByteBuffer getData();
	}
}

