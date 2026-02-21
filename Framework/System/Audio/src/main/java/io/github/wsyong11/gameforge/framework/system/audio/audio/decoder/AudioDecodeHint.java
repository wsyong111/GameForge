package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import java.util.concurrent.ExecutorService;

/**
 * 音频解析策略用于提示 {@link AudioDecoder} 解码时的行为。
 * <p>解码器不兼容的策略可能会被忽略。</p>
 */
public enum AudioDecodeHint {
	/**
	 * 只解码元数据。
	 * <p>解码器兼容时 {@link AudioDecoder#getMetadata()} 将不会完全解析音频数据</p>
	 */
	PRELOAD_METADATA,

	/**
	 * 使用流式解码。
	 * <p>解码器兼容时 {@link AudioDecoder#decode()}，{@link AudioDecoder#decodeAsync(ExecutorService)} 及其派生方法将立刻返回音频流，
	 * 并进行流式解码。</p>
	 * <p>在部分解码器上可能会导致 {@link #PRELOAD_METADATA} 被忽略或缺失部分信息</p>
	 */
	STREAMING,
}
