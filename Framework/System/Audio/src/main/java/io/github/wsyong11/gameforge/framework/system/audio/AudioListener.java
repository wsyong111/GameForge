package io.github.wsyong11.gameforge.framework.system.audio;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Objects;

public interface AudioListener {
	void setPosition(@NotNull Vector3fc position);

	@Contract("_ -> param1")
	@NotNull
	Vector3f getPosition(@NotNull Vector3f dest);

	@NotNull
	default Vector3f getPosition() {
		return this.getPosition(new Vector3f());
	}

	void setVelocity(@NotNull Vector3fc velocity);

	@Contract("_ -> param1")
	@NotNull
	Vector3f getVelocity(@NotNull Vector3f dest);

	@NotNull
	default Vector3f getVelocity() {
		return this.getVelocity(new Vector3f());
	}

	default void setOrientation(@NotNull Vector3fc forward, @NotNull Vector3fc up) {
		Objects.requireNonNull(forward, "forward is null");
		Objects.requireNonNull(up, "up is null");

		this.setOrientationUp(up);
		this.setOrientation(forward);
	}

	void setOrientation(@NotNull Vector3fc forward);

	@Contract("_ -> param1")
	@NotNull
	Vector3f getOrientation(@NotNull Vector3f dest);

	@NotNull
	default Vector3f getOrientation() {
		return this.getOrientation(new Vector3f());
	}

	void setOrientationUp(@NotNull Vector3fc up);

	@Contract("_ -> param1")
	@NotNull
	Vector3f getOrientationUp(@NotNull Vector3f dest);

	@NotNull
	default Vector3f getOrientationUp() {
		return this.getOrientationUp(new Vector3f());
	}
}
