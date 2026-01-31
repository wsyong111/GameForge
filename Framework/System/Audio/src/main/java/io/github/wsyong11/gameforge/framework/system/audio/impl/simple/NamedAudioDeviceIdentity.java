package io.github.wsyong11.gameforge.framework.system.audio.impl.simple;

import io.github.wsyong11.gameforge.framework.system.audio.AudioDeviceIdentity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public class NamedAudioDeviceIdentity implements AudioDeviceIdentity {
	public static final NamedAudioDeviceIdentity EMPTY = new NamedAudioDeviceIdentity("", EMPTY_ID);

	private final String name;
	private final UUID uuid;

	public NamedAudioDeviceIdentity(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");

		this.name = name;
		this.uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
	}

	protected NamedAudioDeviceIdentity(@NotNull String name, @NotNull UUID id) {
		Objects.requireNonNull(name, "name is null");
		Objects.requireNonNull(id, "id is null");

		this.name = name;
		this.uuid = id;
	}

	@NotNull
	@Override
	public String getName() {
		return this.name;
	}

	@NotNull
	public UUID getID() {
		return this.uuid;
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NamedAudioDeviceIdentity that = (NamedAudioDeviceIdentity) o;
		return Objects.equals(this.uuid, that.uuid);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.uuid);
	}

	@NotNull
	@Override
	public String toString() {
		return "AudioDevice{" + this.uuid + " \"" + this.name + "\"}";
	}
}
