package io.github.wsyong11.gameforge.framework.dataflow.ex;

import org.jetbrains.annotations.Nullable;

public class ElementCodecException extends Exception{
	public ElementCodecException() {
	}

	public ElementCodecException(@Nullable String message) {
		super(message);
	}

	public ElementCodecException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public ElementCodecException(@Nullable Throwable cause) {
		super(cause);
	}
}
