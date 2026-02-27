package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.Closeable;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Set;

/**
 * 音频解码器，用于解码音频格式为 {@code PCM} 流。
 *
 * @see AudioDecodeHint
 */
public interface AudioDecoder extends Closeable {
	/**
	 * 获取成功作用于此解码器的提示。
	 *
	 * @return 解码提示列表，通常为不可修改。
	 */
	@NotNull
	@UnmodifiableView
	Set<AudioDecodeHint> getActivatedHints();

	/**
	 * 获取或解析音频中的元数据。
	 *
	 * @return 音频元数据对象
	 * @throws AudioDecodeException 解析元数据途中发生异常
	 * @implNote 实现应在第一次解析后缓存元数据。
	 */
	@NotNull
	AudioMetadata getMetadata() throws AudioDecodeException;

	/**
	 * 尝试预解析音频数据，默认实现为空实现。
	 *
	 * @implNote 此函数应该为静默返回，不应该抛出任何异常。
	 */
	default void preload() {
	}

	/**
	 * 同步解码音频数据。
	 *
	 * @return 已读取的音频帧数量，没有更多数据时将返回 -1
	 * @throws AudioDecodeException 解析 {@code PCM} 数据时发生异常
	 * @see AudioDecodeHint#STREAMING
	 */
	int decode(@NotNull FloatBuffer buffer, int maxFrame) throws AudioDecodeException;

	/**
	 * 解码基本信息对象
	 */
	interface DecodeInfo {
		/**
		 * 打开新的音频数据流。读取位置为文件开头。
		 * <p>尝试关闭流只会使当前流失效而非释放。</p>
		 *
		 * @return 音频数据流。
		 */
		@NotNull
		InputStream openStream();

		/**
		 * 获取传递给解码器的解码提示列表。
		 *
		 * @return 解码提示列表。
		 */
		@NotNull
		@UnmodifiableView
		Set<AudioDecodeHint> getHints();

		/**
		 * 获取音频数据的 {@code Mime} 格式。
		 *
		 * @return 音频数据的格式。
		 */
		@NotNull
		MimeType getMime();
	}
}
