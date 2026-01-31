package io.github.wsyong11.gameforge.framework.system.audio;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface AudioDeviceIdentity {
	UUID EMPTY_ID = new UUID(0L, 0L);

	@NotNull
	String getName();

	// UUID应该是稳定的
	@NotNull
	UUID getID();

	default boolean isEmpty() {
		return EMPTY_ID.equals(this.getID());
	}

	@Override
	boolean equals(@Nullable Object obj);

	@Override
	int hashCode();

	@NotNull
	String toString();
}
