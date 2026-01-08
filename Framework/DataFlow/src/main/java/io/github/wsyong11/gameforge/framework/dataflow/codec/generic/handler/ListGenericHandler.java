package io.github.wsyong11.gameforge.framework.dataflow.codec.generic.handler;

import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericHandler;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.info.GenericHandlerInfoBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class ListGenericHandler implements GenericHandler<List<?>> {
	@NotNull
	@Override
	public Class<? super List<?>> getType() {
		return List.class;
	}

	@Override
	public void buildInfo(@NotNull GenericHandlerInfoBuilder<List<?>> builder) {
		Objects.requireNonNull(builder, "builder is null");

		builder.parameter(0, b -> b
			.collectionResolver(value -> value));
	}
}
