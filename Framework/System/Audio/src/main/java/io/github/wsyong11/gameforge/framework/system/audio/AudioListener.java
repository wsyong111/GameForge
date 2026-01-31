package io.github.wsyong11.gameforge.framework.system.audio;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Objects;

public interface AudioListener {
	void setPosition(@NotNull Vector3fc position);

	@NotNull
	Vector3fc getPosition();

	void setVelocity(@NotNull Vector3fc velocity);

	@NotNull
	Vector3fc getVelocity();

	default void setOrientation(@NotNull Vector3fc forward, @NotNull Vector3fc up) {
		Objects.requireNonNull(forward, "forward is null");
		Objects.requireNonNull(up, "up is null");

		this.setOrientationUp(up);
		this.setOrientation(forward);
	}

	void setOrientation(@NotNull Vector3fc forward);

	@NotNull
	Vector3fc getOrientation();

	void setOrientationUp(@NotNull Vector3fc up);

	@NotNull
	Vector3fc getOrientationUp();

	@NotNull
	@Unmodifiable
	List<AudioPlayer> getAudiblePlayersInRange();
}
