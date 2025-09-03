package io.github.wsyong11.gameforge.framework.text.rich.component;

import io.github.wsyong11.gameforge.framework.text.rich.TextStyle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LiteralTextComponent extends AbstractTextComponent {
	private final String content;

	public LiteralTextComponent(@NotNull TextStyle style, @NotNull String content) {
		super(style);
		Objects.requireNonNull(content, "content is null");
		this.content = content;
	}

	@NotNull
	@Override
	public String getContent() {
		return this.content;
	}
}
