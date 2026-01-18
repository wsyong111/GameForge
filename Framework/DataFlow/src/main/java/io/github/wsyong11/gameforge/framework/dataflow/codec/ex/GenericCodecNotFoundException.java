package io.github.wsyong11.gameforge.framework.dataflow.codec.ex;

import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericCodec;
import org.jetbrains.annotations.Nullable;

public class GenericCodecNotFoundException extends GenericCodecException {
	public GenericCodecNotFoundException() {
	}

	public GenericCodecNotFoundException(@Nullable String message) {
		super(message);
	}

	public GenericCodecNotFoundException(@Nullable String message, @Nullable Throwable cause) {
		super(message, cause);
	}

	public GenericCodecNotFoundException(@Nullable Throwable cause) {
		super(cause);
	}
}
