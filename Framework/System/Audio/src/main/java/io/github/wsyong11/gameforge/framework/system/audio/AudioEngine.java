package io.github.wsyong11.gameforge.framework.system.audio;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface AudioEngine extends AutoCloseable{
	@NotNull
	AudioListener getListener();

	@NotNull
	@Unmodifiable
	List<AudioPlayer> getAudioPlayers();

	@NotNull
	AudioPlayer createPlayer(@NotNull Audio audio);

	float getGlobalVolume();

	void setGlobalVolume(float volume);

	@Override
	void close();
}
