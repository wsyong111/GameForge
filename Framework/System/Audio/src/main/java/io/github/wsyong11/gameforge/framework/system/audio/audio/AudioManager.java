package io.github.wsyong11.gameforge.framework.system.audio.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AudioManager {
	@Nullable
	Audio getAudio(@NotNull Identifier location);

	@Nullable
	Audio getAudioAsync(@NotNull Identifier location);
}
