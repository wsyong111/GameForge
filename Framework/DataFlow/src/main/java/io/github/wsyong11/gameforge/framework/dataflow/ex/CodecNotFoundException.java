package io.github.wsyong11.gameforge.framework.dataflow.ex;

import org.jetbrains.annotations.Nullable;

public class CodecNotFoundException extends ParseException {
	public CodecNotFoundException() {
	}

	public CodecNotFoundException(@Nullable String message) {
		super(message);
	}

	public CodecNotFoundException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public CodecNotFoundException(@Nullable Throwable cause) {
		super(cause);
	}
}
