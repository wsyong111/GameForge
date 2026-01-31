package io.github.wsyong11.gameforge.framework.system.audio.impl.openal;

import io.github.wsyong11.gameforge.framework.system.audio.AudioListener;
import io.github.wsyong11.gameforge.framework.system.audio.AudioPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector3fc;

import java.util.List;

public class OpenALListener implements AudioListener {
	@Override
	public void setPosition(@NotNull Vector3fc position) {

	}

	@Override
	public @NotNull Vector3fc getPosition() {
		return null;
	}

	@Override
	public void setVelocity(@NotNull Vector3fc velocity) {

	}

	@Override
	public @NotNull Vector3fc getVelocity() {
		return null;
	}

	@Override
	public void setOrientation(@NotNull Vector3fc forward) {

	}

	@Override
	public @NotNull Vector3fc getOrientation() {
		return null;
	}

	@Override
	public void setOrientationUp(@NotNull Vector3fc up) {

	}

	@Override
	public @NotNull Vector3fc getOrientationUp() {
		return null;
	}

	@Override
	public @NotNull @Unmodifiable List<AudioPlayer> getAudiblePlayersInRange() {
		return List.of();
	}
}
