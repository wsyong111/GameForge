package io.github.wsyong11.gameforge.game.core.server.prompt;

import org.jetbrains.annotations.NotNull;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedStringBuilder;

public interface PromptHighlighter {
	void highlight(@NotNull LineReader reader, @NotNull String buffer, @NotNull AttributedStringBuilder builder);
}
