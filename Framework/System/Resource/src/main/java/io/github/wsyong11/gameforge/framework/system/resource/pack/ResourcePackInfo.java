package io.github.wsyong11.gameforge.framework.system.resource.pack;

import io.github.wsyong11.gameforge.framework.text.rich.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;


public interface ResourcePackInfo {
	@Nullable
	TextComponent getDescription();

	@Nullable
	@Unmodifiable
	List<String> getAuthors();

	@NotNull
	String getVersion();
}
