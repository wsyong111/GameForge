package io.github.wsyong11.gameforge.framework.system.render.mesh.ex;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class MeshLoadException extends IOException {
	public MeshLoadException() {
	}

	public MeshLoadException(@Nullable String message) {
		super(message);
	}

	public MeshLoadException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public MeshLoadException(@Nullable Throwable cause) {
		super(cause);
	}
}
