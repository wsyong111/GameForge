package io.github.wsyong11.gameforge.framework.system.audio.audio.pcm;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * PCMList 表示一个可变长度的 PCM 音频数据列表。
 * <p>
 * 与 {@link PCMArray} 不同，PCMList 支持动态添加、删除帧数据，
 * 适用于音频拼接、缓冲、编辑等场景。
 * </p>
 */
public interface PCMList extends PCMArray {
	/**
	 * 获取当前可用的最大容量（帧数）。
	 *
	 * @return 最大容量（帧数）
	 */
	int getMaxCapacity();

	/**
	 * 判断是否为空。
	 *
	 * @return 如果不包含任何帧则返回 {@code true}
	 */
	default boolean isEmpty() {
		return this.getFrames() <= 0;
	}

	/**
	 * 在末尾追加 PCM 数据。
	 * <p>
	 * 数据按帧顺序写入，每帧内的采样值按通道顺序排列。
	 * </p>
	 *
	 * @param samples 源采样数组
	 * @return 实际写入的帧数
	 * @throws NullPointerException     如果数组为 {@code null}
	 * @throws IllegalArgumentException 如果数组长度不能构成完整帧
	 */
	int add(float @NotNull [] samples);

	/**
	 * 在指定帧位置插入 PCM 数据。
	 *
	 * @param samples 源采样数组
	 * @param frame   插入位置的帧索引
	 * @return 实际写入的帧数
	 * @throws NullPointerException      如果数组为 {@code null}
	 * @throws IndexOutOfBoundsException 如果帧索引超出范围
	 * @throws IllegalArgumentException  如果数组长度不能构成完整帧
	 * @see #add(float[], int, int, int)
	 */
	default int add(float @NotNull [] samples, int frame) {
		return this.add(samples, 0, samples.length, frame);
	}

	/**
	 * 在指定帧位置插入 PCM 数据。
	 * <p>
	 * 数据按帧顺序写入，每帧内的采样值按通道顺序排列。
	 * 如果提供的数据不足完整帧，则忽略末尾不完整部分。
	 * </p>
	 *
	 * @param samples 源采样数组
	 * @param offset  起始读取索引
	 * @param length  写入的采样总数（非帧数）
	 * @param frame   插入位置的帧索引
	 * @return 实际写入的帧数
	 * @throws NullPointerException      如果数组为 {@code null}
	 * @throws IndexOutOfBoundsException 如果索引或帧位置超出范围
	 */
	int add(float @NotNull [] samples, int offset, int length, int frame);

	/**
	 * 在末尾追加另一个 PCM 数据。
	 *
	 * @param array 源 PCM 数据
	 * @return 实际写入的帧数
	 * @throws NullPointerException     如果参数为 {@code null}
	 * @throws IllegalArgumentException 如果通道数不一致
	 */
	int add(@NotNull PCMArray array);

	/**
	 * 在指定帧位置插入另一个 PCM 数据。
	 *
	 * @param array 源 PCM 数据
	 * @param frame 插入位置的帧索引
	 * @return 实际写入的帧数
	 * @throws NullPointerException      如果参数为 {@code null}
	 * @throws IndexOutOfBoundsException 如果帧索引超出范围
	 * @throws IllegalArgumentException  如果通道数不一致
	 * @see #add(PCMArray, int, int, int)
	 */
	default int add(@NotNull PCMArray array, int frame) {
		Objects.requireNonNull(array, "array is null");
		return this.add(array, 0, array.getFrames(), frame);
	}

	/**
	 * 在指定帧位置插入另一个 PCM 数据的子区间。
	 * <p>
	 * 数据按帧顺序写入，每帧内的采样值按通道顺序排列。
	 * </p>
	 *
	 * @param array  源 PCM 数据
	 * @param offset 起始帧索引
	 * @param length 写入的帧数
	 * @param frame  插入位置的帧索引
	 * @return 实际写入的帧数
	 * @throws NullPointerException      如果参数为 {@code null}
	 * @throws IndexOutOfBoundsException 如果索引超出范围
	 * @throws IllegalArgumentException  如果通道数不一致
	 */
	int add(@NotNull PCMArray array, int offset, int length, int frame);

	/**
	 * 删除指定帧。
	 *
	 * @param frame 帧索引
	 * @throws IndexOutOfBoundsException 如果帧索引超出范围
	 */
	void remove(int frame);

	/**
	 * 删除指定范围的帧。
	 *
	 * @param frame  起始帧索引
	 * @param length 删除的帧数
	 * @throws IndexOutOfBoundsException 如果范围超出有效区间
	 */
	void remove(int frame, int length);

	default int getAndRemove(float @NotNull [] dest, int frame) {
		Objects.requireNonNull(dest, "dest is null");
		return this.getAndRemove(dest, 0, dest.length, frame);
	}

	default int getAndRemove(float @NotNull [] dest, int offset, int length, int frame) {
		Objects.requireNonNull(dest, "dest is null");
		Objects.checkFromIndexSize(offset, length, dest.length);

		int consumed = this.getFrame(dest, offset, length, frame);
		this.remove(frame, consumed);
		return consumed;
	}

	/**
	 * 收缩内部存储以匹配当前数据大小。
	 * <p>
	 * 默认实现为空操作，具体行为由实现类决定。
	 * </p>
	 */
	default void trim() {
	}

	/**
	 * 清空所有 PCM 数据。
	 * <p>
	 * 调用后帧数将变为 0。
	 * </p>
	 */
	void clear();
}