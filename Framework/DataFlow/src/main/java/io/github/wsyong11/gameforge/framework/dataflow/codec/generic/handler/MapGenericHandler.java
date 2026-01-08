package io.github.wsyong11.gameforge.framework.dataflow.codec.generic.handler;

import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericHandler;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.info.GenericHandlerInfoBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MapGenericHandler implements GenericHandler<Map<?, ?>> {
	@NotNull
	@Override
	public Class<? super Map<?, ?>> getType() {
		return Map.class;
	}

	@Override
	public void buildInfo(@NotNull GenericHandlerInfoBuilder<Map<?, ?>> builder) {
		builder
			.parameter(0, b -> b
				.collectionResolver(Map::keySet))
			.parameter(1, b -> b
				.collectionResolver(Map::values));
	}
}
