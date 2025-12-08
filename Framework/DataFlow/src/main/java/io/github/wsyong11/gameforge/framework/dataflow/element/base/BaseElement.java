package io.github.wsyong11.gameforge.framework.dataflow.element.base;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 元素层次结构中所有元素的基本接口
 * <p>该接口充当两个 {@link Element} （不可变）和 {@link MutableElement} （可变）元素的公共超类型。
 * 通常<strong>为了确保代码的健壮性，不建议将此接口作为字段类型，函数参数和返回值，
 * 建议通过 {@link Element} 和 {@link MutableElement} 作为类型</strong></p>
 */
public interface BaseElement {
	@Override
	boolean equals(@Nullable Object o);

	@Override
	int hashCode();

	@Override
	@NotNull
	String toString();
}
