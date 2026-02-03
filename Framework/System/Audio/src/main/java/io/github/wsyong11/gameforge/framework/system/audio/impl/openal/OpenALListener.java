package io.github.wsyong11.gameforge.framework.system.audio.impl.openal;

import io.github.wsyong11.gameforge.framework.system.audio.AudioListener;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.system.MemoryUtil;

import java.io.Closeable;
import java.nio.FloatBuffer;
import java.util.Objects;

public class OpenALListener implements AudioListener, Closeable {
	private final Vector3f position;
	private final Vector3f velocity;
	private final Vector3f orientationForward;
	private final Vector3f orientationUp;

	private boolean changed;

	private final FloatBuffer orientationBuffer;

	public OpenALListener() {
		this.position = new Vector3f(0, 0, 0);
		this.velocity = new Vector3f(0, 0, 0);
		this.orientationForward = new Vector3f(0, 0, -1);
		this.orientationUp = new Vector3f(0, 1, 0);

		this.changed = false;

		this.orientationBuffer = MemoryUtil.memCallocFloat(6);
	}

	@Override
	public void setPosition(@NotNull Vector3fc position) {
		Objects.requireNonNull(position, "position is null");

		if (this.position.equals(position, 1e-6F))
			return;

		this.position.set(position);
		this.changed = true;
	}

	@NotNull
	@Override
	public Vector3f getPosition(@NotNull Vector3f dest) {
		Objects.requireNonNull(dest, "dest is null");
		dest.set(this.position);
		return dest;
	}

	@Override
	public void setVelocity(@NotNull Vector3fc velocity) {
		Objects.requireNonNull(velocity, "velocity is null");

		if (this.velocity.equals(velocity, 1e-6F))
			return;

		this.velocity.set(velocity);
		this.changed = true;
	}

	@NotNull
	@Override
	public Vector3f getVelocity(@NotNull Vector3f dest) {
		Objects.requireNonNull(dest, "dest is null");
		dest.set(this.velocity);
		return dest;
	}

	// -------------------------------------------------------------------------------------------------------------- //


	@Override
	public void setOrientation(@NotNull Vector3fc forward, @NotNull Vector3fc up) {
		Objects.requireNonNull(forward, "forward is null");
		Objects.requireNonNull(up, "up is null");

		if (this.orientationForward.equals(forward, 1e-6F) && this.orientationUp.equals(up, 1e-6F))
			return;

		this.orientationForward.set(forward);
		this.orientationUp.set(up);
		this.changed = true;
		this.updateOrientationBuffer();
	}

	@Override
	public void setOrientation(@NotNull Vector3fc forward) {
		Objects.requireNonNull(forward, "forward is null");

		if (this.orientationForward.equals(forward, 1e-6F))
			return;

		this.orientationForward.set(forward);
		this.changed = true;
		this.updateOrientationBuffer();
	}

	@NotNull
	@Override
	public Vector3f getOrientation(@NotNull Vector3f dest) {
		Objects.requireNonNull(dest, "dest is null");
		dest.set(this.orientationForward);
		return dest;
	}

	@Override
	public void setOrientationUp(@NotNull Vector3fc up) {
		Objects.requireNonNull(up, "up is null");

		if (this.orientationUp.equals(up, 1e-6F))
			return;

		this.orientationUp.set(up);
		this.changed = true;
		this.updateOrientationBuffer();
	}

	@NotNull
	@Override
	public Vector3f getOrientationUp(@NotNull Vector3f dest) {
		Objects.requireNonNull(dest, "dest is null");
		dest.set(this.orientationUp);
		return dest;
	}

	private void updateOrientationBuffer() {
		this.orientationBuffer.put(0, this.orientationForward.x);
		this.orientationBuffer.put(1, this.orientationForward.y);
		this.orientationBuffer.put(2, this.orientationForward.z);
		this.orientationBuffer.put(3, this.orientationUp.x);
		this.orientationBuffer.put(4, this.orientationUp.y);
		this.orientationBuffer.put(5, this.orientationUp.z);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public Vector3fc getPositionRaw() {
		return this.position;
	}

	@NotNull
	public Vector3fc getVelocityRaw() {
		return this.velocity;
	}

	@NotNull
	public Vector3fc getOrientationRaw() {
		return this.orientationForward;
	}

	@NotNull
	public Vector3fc getOrientationUpRaw() {
		return this.orientationUp;
	}

	@NotNull
	public FloatBuffer getOrientationBuffer() {
		return this.orientationBuffer;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public boolean isChanged() {
		boolean changed = this.changed;
		this.changed = false;
		return changed;
	}

	@Override
	public void close() {
		MemoryUtil.memFree(this.orientationBuffer);
	}
}
