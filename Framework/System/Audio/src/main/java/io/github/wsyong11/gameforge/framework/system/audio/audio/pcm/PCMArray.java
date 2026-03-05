package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;

/**
 * PCMArray 表示一个多通道 PCM 音频数据的抽象。
 * <p>
 * PCM 数据按帧组织，每帧包含每个通道的一个采样值。
 * 所有采样值均为浮点数，范围为 {@code [-1.0, 1.0]}。
 * </p>
 */
public interface PCMArray {
	/**
	 * 单声道。
	 */
	int CHANNEL_MONO = 1;

	/**
	 * 立体声（左右声道）。
	 */
	int CHANNEL_STEREO = 2;

	/**
	 * 四声道环绕声（前左、前右、后左、后右）。
	 */
	int CHANNEL_QUAD = 4;

	/**
	 * 5.1 环绕声（前左、前右、前中、低音、后左、后右）。
	 */
	int CHANNEL_FIVE_POINT_ONE = 6;

	/**
	 * 7.1 环绕声（前左、前右、前中、低音、后左、后右、侧左、侧右）。
	 */
	int CHANNEL_SEVEN_POINT_ONE = 8;

	/**
	 * 获取音频通道数。
	 *
	 * @return 音频通道数
	 */
	int getChannels();

	/**
	 * 获取音频采样率。
	 *
	 * @return 每秒采样率（Hz）
	 */
	int getSampleRate();

	/**
	 * 获取总帧数。
	 *
	 * @return 音频总帧数
	 */
	long getFrames();

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取指定通道和帧的采样值。
	 *
	 * @param channel 通道索引
	 * @param frame   帧索引
	 * @return 指定通道和帧的采样值，范围为 {@code [-1.0, 1.0]}
	 * @throws IndexOutOfBoundsException 如果通道索引或帧索引超出有效范围
	 */
	float getSample(int channel, long frame);

	/**
	 * 批量读取 PCM 数据到数组。
	 * <p>
	 * 数据按帧顺序存储，每帧内的采样值按通道顺序排列。
	 * 如果数组容量不足以容纳完整帧，会按数组容量截断。
	 * </p>
	 *
	 * @param array  目标数组
	 * @param index  起始写入索引
	 * @param length 读取的采样总数（非帧数）
	 * @param frame  起始帧索引
	 * @return 实际读取的帧数
	 * @throws IndexOutOfBoundsException 如果起始帧或数组索引超出范围
	 */
	int getFrame(float @NotNull [] array, int index, int length, long frame);

	/**
	 * 批量读取 PCM 数据到数组，从索引 0 开始。
	 *
	 * @param array 目标数组
	 * @param frame 起始帧索引
	 * @return 实际读取的帧数
	 * @throws IndexOutOfBoundsException 如果起始帧索引超出范围
	 * @see #getFrame(float[], int, int, long)
	 */
	default int getFrame(float @NotNull [] array, long frame) {
		return this.getFrame(array, 0, array.length, frame);
	}

	/**
	 * 批量读取 PCM 数据到 {@link FloatBuffer}。
	 * <p>
	 * 数据按帧顺序存储，每帧内的采样值按通道顺序排列。
	 * 如果缓冲区容量不足以容纳完整帧，会按缓冲区容量截断。
	 * </p>
	 *
	 * @param dst        目标缓冲区
	 * @param frame      起始帧索引
	 * @param frameCount 读取的帧数
	 * @return 实际读取的帧数
	 * @throws IndexOutOfBoundsException 如果起始帧超出范围
	 * @see #getFrame(float[], int, int, long)
	 */
	int getFrame(@NotNull FloatBuffer dst, long frame, int frameCount);

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 设置指定通道和帧的采样值。
	 *
	 * @param channel 通道索引
	 * @param frame   帧索引
	 * @param value   采样值，写入时会被限制在 {@code [-1.0, 1.0]} 范围
	 * @throws IndexOutOfBoundsException 如果通道索引或帧索引超出有效范围
	 */
	void setSample(int channel, long frame, float value);

	/**
	 * 批量写入 PCM 数据到数组。
	 * <p>
	 * 数据按帧顺序存储，每帧内的采样值按通道顺序排列。
	 * 如果数组末尾包含不完整的帧（采样数不足一个完整帧），则忽略。
	 * </p>
	 *
	 * @param array  源数组
	 * @param index  起始读取索引
	 * @param length 写入的采样总数（非帧数）
	 * @param frame  起始帧索引
	 * @return 实际写入的帧数
	 * @throws IndexOutOfBoundsException 如果起始帧或数组索引超出范围
	 */
	int setFrame(float @NotNull [] array, int index, int length, long frame);

	/**
	 * 批量写入 PCM 数据到数组，从索引 0 开始。
	 *
	 * @param array 源数组
	 * @param frame 起始帧索引
	 * @return 实际写入的帧数
	 * @throws IndexOutOfBoundsException 如果起始帧索引超出范围
	 * @see #setFrame(float[], int, int, long)
	 */
	default int setFrame(float @NotNull [] array, long frame) {
		return this.setFrame(array, 0, array.length, frame);
	}

	/**
	 * 批量写入 PCM 数据到 {@link FloatBuffer}。
	 * <p>
	 * 数据按帧顺序存储，每帧内的采样值按通道顺序排列。
	 * 如果缓冲区末尾包含不完整帧（采样数不足一个完整帧），则忽略。
	 * </p>
	 *
	 * @param src        源缓冲区
	 * @param frame      起始帧索引
	 * @param frameCount 写入的帧数
	 * @return 实际写入的帧数
	 * @throws IndexOutOfBoundsException 如果起始帧索引超出范围
	 */
	int setFrame(@NotNull FloatBuffer src, long frame, int frameCount);
}
