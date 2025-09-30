package io.github.wsyong11.gameforge.framework.system.window.icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector2ic;

import java.io.Closeable;
import java.nio.ByteBuffer;
import java.util.List;

public interface Icon extends Closeable {
	boolean isClosed();

	@NotNull
	@Unmodifiable
	List<Vector2ic> getSizes();

	/**
	 * 获取指定尺寸索引的像素数据。返回的 {@link ByteBuffer} 为 RGBA8 格式，
	 * row-major 排列
	 *
	 * @param index 尺寸索引，可通过 {@link #getSizes()} 获取可用的大小，和列表的索引对应
	 * @return 只读像素缓冲区，position为0，limit和capacity都为数据长度
	 * @throws IndexOutOfBoundsException 尺寸索引超出访问时抛出
	 * @throws IllegalStateException     在图标已关闭时抛出
	 */
	@NotNull
	ByteBuffer getImage(int index);

	@Override
	void close();
}
