package io.github.wsyong11.gameforge.framework.text.rich.component;

import io.github.wsyong11.gameforge.framework.text.rich.TextComponent;
import io.github.wsyong11.gameforge.framework.text.rich.TextStyle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class AbstractTextComponent implements TextComponent {
	private final TextStyle style;

	protected AbstractTextComponent(@NotNull TextStyle style) {
		Objects.requireNonNull(style, "style is null");
		this.style = style;
	}

	@NotNull
	@Override
	public TextStyle getStyle() {
		return this.style;
	}

	@NotNull
	@Override
	public String toString() {
		return this.getContent();
	}
}
