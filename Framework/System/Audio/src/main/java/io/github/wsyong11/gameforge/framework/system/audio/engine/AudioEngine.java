package io.github.wsyong11.gameforge.framework.system.audio.engine;

import io.github.wsyong11.gameforge.framework.system.audio.AudioDevice;
import io.github.wsyong11.gameforge.framework.system.audio.AudioDeviceIdentity;
import io.github.wsyong11.gameforge.framework.system.audio.AudioListener;
import io.github.wsyong11.gameforge.framework.system.audio.AudioPlayer;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import io.github.wsyong11.gameforge.framework.system.audio.ex.AudioDeviceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

public interface AudioEngine extends AutoCloseable {
	@NotNull
	String getName();

	void init();

	int getLoopPreSec();

	void loopTick();

	@NotNull
	AudioManager getAudioManager();

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	AudioListener getListener();

	@NotNull
	AudioPlayer createPlayer(@NotNull Audio audio);

	@NotNull
	@Unmodifiable
	List<AudioPlayer> getAudioPlayers();

	float getGlobalVolume();

	// 0-1
	void setGlobalVolume(float volume);

	// -------------------------------------------------------------------------------------------------------------- //

	// 如果无法枚举则只返回系统默认输出设备
	@NotNull
	List<AudioDeviceIdentity> getDevices();

	void setActiveDevice(@NotNull AudioDeviceIdentity device);

	// 默认返回系统默认输出设备
	@NotNull
	AudioDeviceIdentity getActiveDevice();

	@NotNull
	AudioDeviceIdentity getDefaultDevice();

	@Nullable
	AudioDevice getDevice(@NotNull AudioDeviceIdentity identity) throws AudioDeviceException;

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	void close();
}
