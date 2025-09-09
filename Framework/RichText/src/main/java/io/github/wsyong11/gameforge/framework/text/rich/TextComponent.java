package io.github.wsyong11.gameforge.framework.text.rich;

import org.jetbrains.annotations.NotNull;

public interface TextComponent {
	@NotNull
	String getContent();

	@NotNull
	TextStyle getStyle();
}
