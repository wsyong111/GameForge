package io.github.wsyong11.gameforge.framework.config.codec;

import io.github.wsyong11.gameforge.framework.config.ex.ValueCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.ToDoubleBiFunction;

// TODO 2025/12/16: 用CodecContext实现嵌套Codec
/**
 * {@code ValueCodec} 用于在 Java 对象与 {@link Element} 表示之间进行双向转换。
 * <p>
 * 编码与解码过程中会传入 {@code type} 参数，该类型来自：
 * <ul>
 *     <li>编码：由 {@link CodecMap#encode(Object, Class)} 提供。</li>
 *     <li>解码：由 {@link CodecMap#decode(Element, Class)} 指定的期望类型。</li>
 * </ul>
 * 实现应根据实际类型和元素内容执行正确的转换。
 * </p>
 *
 * @param <T> 此编解码器处理的值类型。
 */
public interface ValueCodec<T> {
	/**
	 * 将指定的值编码为 {@link Element}。
	 * <p>
	 * {@code type} 参数为调用方传入的实际类型，
	 * 通常来自 {@link CodecMap#encode(Object, Class)} 中提供的类型信息。
	 * 实现可以根据此类型执行适配或特殊处理。
	 * </p>
	 *
	 * @param value 要编码的值，不允许为 {@code null}。
	 * @param type  值的具体类型，不允许为 {@code null}。
	 * @return 对应的 {@link Element} 表示，不能为 {@code null}。
	 * @throws ValueCodecException 当值无法编码时抛出。
	 */
	@NotNull
	Element encode(@NotNull CodecContext ctx, @NotNull T value, @NotNull Class<? extends T> type) throws ValueCodecException;

    /**
     * 从 {@link Element} 解码为指定类型的值。
     * <p>
     * {@code type} 由调用者指定，来源于 {@link CodecMap#decode(Element, Class)} 的期望类型。
     * 编解码器应根据 {@code type} 判断是否支持该转换。
     * </p>
     *
     * @param element 源数据，不允许为 {@code null}。
     * @param type    期望解码出的类型，不允许为 {@code null}。
     * @return 解码得到的值，不允许为 {@code null}。
     * @throws ValueCodecException 当元素无法解码为指定类型时抛出。
     */
	@NotNull
	T decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends T> type) throws ValueCodecException;

    /**
     * 获取此编解码器能够处理的 Java 类型集合。
     * <p>
     * {@link CodecMap} 会根据此集合匹配合适的编解码器。
     * </p>
     *
     * @return 支持的类型集合，不允许为 {@code null} 且必须非空。
     */
	@NotNull
	@Deprecated
	Set<Class<? extends T>> getSupportTypes();

	boolean isSupportType(@NotNull Class<?extends T> type);

    /**
     * 判断是否支持指定的值。
     * <p>
     * 默认总是返回 {@code true}，具体实现可以根据值内容决定是否支持。
     * </p>
     *
     * @param value 要判断的值，不允许为 {@code null}。
     * @return 如果支持此值返回 {@code true}。
     */
	default boolean isSupportedValue(@NotNull T value) {
		return true;
	}

    /**
     * 判断是否支持指定的 {@link Element}。
     * <p>
     * 默认总是返回 {@code true}，实际实现可以进行结构检查。
     * </p>
     *
     * @param value 要判断的元素，不允许为 {@code null}。
     * @return 如果支持此元素返回 {@code true}。
     */
	default boolean isSupportedElement(@NotNull Element value) {
		return true;
	}
}
