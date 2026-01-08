package io.github.wsyong11.gameforge.framework.config.codec;

import io.github.wsyong11.gameforge.framework.config.ex.ValueCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;

// TODO 2025/12/16: 用CodecContext实现嵌套Codec

/**
 * {@code ValueCodec} 用于在 Java 对象与 {@link Element} 表示之间进行双向转换。
 * <p>
 * 编解码过程发生在配置系统的数据流中，主要职责是：
 * <ul>
 *     <li>将 Java 对象编码为统一的数据表示 {@link Element}</li>
 *     <li>将 {@link Element} 解码为调用方期望的 Java 类型</li>
 * </ul>
 * </p>
 *
 * <h2>类型参数说明</h2>
 * <p>
 * 所有编解码方法都会显式接收一个 {@code type} 参数，用于描述调用方
 * <em>期望的具体类型</em>：
 * </p>
 * <ul>
 *     <li>
 *         编码阶段：
 *         {@code type} 由 {@link CodecMap#encode(Object, Class)} 提供，
 *         表示原始值的静态类型或调用方指定的目标类型。
 *     </li>
 *     <li>
 *         解码阶段：
 *         {@code type} 由 {@link CodecMap#decode(Element, Class)} 指定，
 *         表示期望解码得到的 Java 类型。
 *     </li>
 * </ul>
 *
 * <p>
 * 实现者应根据 {@code type} 与 {@link Element} 的实际内容，
 * 判断是否支持该转换，并在不支持时抛出 {@link ValueCodecException}。
 * </p>
 *
 * @param <T> 此编解码器主要处理的 Java 值类型。
 */
public interface ValueCodec<T> {
	/**
	 * 将指定的 Java 值编码为 {@link Element}。
	 * <p>
	 * {@code type} 参数表示调用方传入的值类型信息，
	 * 其来源通常是 {@link CodecMap#encode(Object, Class)}。
	 * </p>
	 *
	 * <p>
	 * 实现可以根据 {@code type}：
	 * <ul>
	 *     <li>区分同一父类型的不同子类型</li>
	 *     <li>执行特殊适配或类型映射</li>
	 *     <li>拒绝不支持的类型</li>
	 * </ul>
	 * </p>
	 *
	 * @param ctx   编解码上下文，用于提供额外环境信息，不允许为 {@code null}。
	 * @param value 要编码的值，不允许为 {@code null}。
	 * @param type  值的具体类型，不允许为 {@code null}。
	 * @return 对应的 {@link Element} 表示，不能为 {@code null}。
	 * @throws ValueCodecException 当值无法被此编解码器编码时抛出。
	 */
	@NotNull
	Element encode(
		@NotNull CodecContext ctx,
		@NotNull T value,
		@NotNull Class<? extends T> type
	) throws ValueCodecException;

	/**
	 * 将 {@link Element} 解码为指定类型的 Java 值。
	 * <p>
	 * {@code type} 由调用者指定，通常来源于
	 * {@link CodecMap#decode(Element, Class)} 的期望类型参数。
	 * </p>
	 *
	 * <p>
	 * 实现应根据 {@link Element} 的实际结构以及 {@code type}：
	 * <ul>
	 *     <li>判断是否支持该类型转换</li>
	 *     <li>执行必要的类型检查与转换</li>
	 *     <li>在无法解码时抛出异常</li>
	 * </ul>
	 * </p>
	 *
	 * @param ctx     编解码上下文，用于提供额外环境信息，不允许为 {@code null}。
	 * @param element 源数据元素，不允许为 {@code null}。
	 * @param type    期望解码得到的 Java 类型，不允许为 {@code null}。
	 * @return 解码后的值，不允许为 {@code null}。
	 * @throws ValueCodecException 当元素无法解码为指定类型时抛出。
	 */
	@NotNull
	T decode(
		@NotNull CodecContext ctx,
		@NotNull Element element,
		@NotNull Class<? extends T> type
	) throws ValueCodecException;

	/**
	 * 判断此编解码器是否支持指定的 Java 类型。
	 *
	 * @param type 要判断的类型，不允许为 {@code null}。
	 * @return 若支持该类型返回 {@code true}。
	 */
	boolean isSupportType(@NotNull Class<? extends T> type);

	/**
	 * 判断是否支持指定的 Java 值实例。
	 * <p>
	 * 默认实现始终返回 {@code true}，
	 * 具体实现可以根据值的内容或状态决定是否支持。
	 * </p>
	 *
	 * @param type  值的具体类型，不允许为 {@code null}。
	 * @param value 要判断的值，不允许为 {@code null}。
	 * @return 如果支持该值返回 {@code true}。
	 */
	default boolean isSupportValue(@NotNull Class<? extends T> type, @NotNull T value) {
		return true;
	}

	/**
	 * 判断是否支持指定的 {@link Element} 结构。
	 * <p>
	 * 默认实现始终返回 {@code true}，
	 * 实际实现可以对 {@link Element} 的类型或内部结构进行校验。
	 * </p>
	 *
	 * @param type    期望解码得到的 Java 类型，不允许为 {@code null}。
	 * @param element 要判断的元素，不允许为 {@code null}。
	 * @return 如果支持该元素返回 {@code true}。
	 */
	default boolean isSupportElement(@NotNull Class<? extends T> type, @NotNull Element element) {
		return true;
	}
}
