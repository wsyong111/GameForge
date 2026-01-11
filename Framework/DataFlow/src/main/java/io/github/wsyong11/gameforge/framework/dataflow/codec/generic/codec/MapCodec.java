package io.github.wsyong11.gameforge.framework.dataflow.codec.generic.codec;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericCodec;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.GenericInfo;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.ResolvedParameterType;
import io.github.wsyong11.gameforge.framework.dataflow.codec.generic.TypeVariableToken;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MapCodec implements GenericCodec<Map<?, ?>> {
	private static  final TypeVariableToken K = TypeVariableToken.of(Map.class, 0);
	private static  final TypeVariableToken V = TypeVariableToken.of(Map.class, 1);

	@NotNull
	@Override
	public Class<? super Map<?, ?>> getType() {
		return Map.class;
	}

	@Override
	public void buildInfo(@NotNull GenericInfo.Builder<Map<?, ?>> builder) {

	}

	@NotNull
	@Override
	public Map<?, ?> decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull ResolvedParameterType types) throws CodecException {
		return Map.of();
	}

	@NotNull
	@Override
	public Element encode(@NotNull CodecContext ctx, @NotNull Map<?, ?> value, @NotNull ResolvedParameterType types) throws CodecException {
		return null;
	}

	@Override
	public boolean isSupportElement(@NotNull CodecContext ctx, @NotNull Element element) {
		return GenericCodec.super.isSupportElement(ctx, element);
	}

	@Override
	public boolean isSupportValue(@NotNull CodecContext ctx, @NotNull Map<?, ?> value) {
		return GenericCodec.super.isSupportValue(ctx, value);
	}
}
