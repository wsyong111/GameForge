package io.github.wsyong11.gameforge.framework.system.graphic.core.info;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface RenderDeviceInfo {
	@NotNull
	String getDeviceName();

	@NotNull
	String getVendorName();

	@NotNull
	UUID getId();

	// Bytes, -1 unknown
	long getVram();

	@NotNull
	RenderBackedInfo getBacked();

	@Nullable
	<T> T getFuture(@NotNull RenderFutureKey<T> key);

	@Contract("_, !null -> param2")
	@Nullable
	default <T> T getFuture(@NotNull RenderFutureKey<T> key, @Nullable T defaultValue) {
		T future = this.getFuture(key);
		return future == null ? defaultValue : future;
	}

	boolean hasFuture(@NotNull RenderFutureKey<?> key);
}
