package io.github.wsyong11.gameforge.framework.system.audio;

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

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	AudioListener getListener();

	@NotNull
	@Unmodifiable
	List<AudioPlayer> getAudioPlayers();

	@NotNull
	AudioPlayer createPlayer(@NotNull Audio audio);

	float getGlobalVolume();

	// 0-1
	void setGlobalVolume(float volume);

	// -------------------------------------------------------------------------------------------------------------- //

	// 如果无法枚举则只返回系统默认输出设备
	@NotNull
	List<AudioDeviceIdentity> getDevices();

	void setOutputDevice(@NotNull AudioDeviceIdentity device) throws AudioDeviceException;

	// 默认返回系统默认输出设备
	@NotNull
	AudioDeviceIdentity getOutputDevice();

	@NotNull
	AudioDeviceIdentity getDefaultDevice();

	@Nullable
	AudioDevice getDevice(@NotNull AudioDeviceIdentity identity) throws AudioDeviceException;

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	void close();
}
