package io.github.wsyong11.gameforge.framework.dataflow.element.mutable;

import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.dataflow.element.NullElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.base.BaseElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MutableElement extends BaseElement {
	@NotNull
	static Element asElementSafe(@Nullable MutableElement element) {
		return element == null ? NullElement.INSTANCE : element.asElement();
	}

	@NotNull
	Element asElement();
}
