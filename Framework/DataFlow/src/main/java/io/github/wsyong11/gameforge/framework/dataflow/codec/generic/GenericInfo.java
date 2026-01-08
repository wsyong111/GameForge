package io.github.wsyong11.gameforge.framework.dataflow.codec.generic;

import com.google.common.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GenericInfo<T> {
	private final Class<T> type;
	private final List<GenericParameterInfo<T>> parameters;
	private final GenericEncoder<T> encoder;
	private final GenericDecoder<T> decoder;

	@SuppressWarnings("unchecked")
	@NotNull
	public static <V> Builder<V> builder(@NotNull TypeToken<V> type) {
		Objects.requireNonNull(type, "type is null");
		return (Builder<V>) new Builder<>(type.getRawType());
	}

	public GenericInfo(
		@NotNull Class<T> type,
		@NotNull List<GenericParameterInfo<T>> parameters,
		@NotNull GenericEncoder<T> encoder,
		@NotNull GenericDecoder<T> decoder
	) {
		Objects.requireNonNull(type, "type is null");
		Objects.requireNonNull(parameters, "parameters is null");
		Objects.requireNonNull(encoder, "encoder is null");
		Objects.requireNonNull(decoder, "decoder is null");

		this.type = type;
		this.parameters = List.copyOf(parameters);
		this.encoder = encoder;
		this.decoder = decoder;

		if (this.parameters.size() != type.getTypeParameters().length)
			throw new IllegalArgumentException("Parameter length not same of type parameters length");

		for (int i = 0; i < this.parameters.size(); i++) {
			GenericParameterInfo<T> parameter = this.parameters.get(i);
			if (parameter.getType() != type)
				throw new IllegalArgumentException("Parameter from index " + i + " is not same of class type");
		}
	}

	@NotNull
	public Class<T> getType() {
		return this.type;
	}

	@NotNull
	@UnmodifiableView
	public List<GenericParameterInfo<T>> getParameters() {
		return this.parameters;
	}

	@NotNull
	public GenericEncoder<T> getEncoder() {
		return this.encoder;
	}

	@NotNull
	public GenericDecoder<T> getDecoder() {
		return this.decoder;
	}

	@Override
	public String toString() {
		TypeVariable<Class<T>>[] typeParameters = this.type.getTypeParameters();
		String parameters = IntStream
			.range(0, typeParameters.length)
			.mapToObj(i -> this.parameters.get(i).isIgnore() ? "*" : typeParameters[i].getName())
			.collect(Collectors.joining(", "));

		return "GenericInfo(" + this.type + "<" + parameters + ">)";
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static class Builder<V> {
		private final Class<V> type;
		private final List<GenericParameterInfo<V>> parameters;
		private final int parameterCount;

		private GenericEncoder<V> encoder;
		private GenericDecoder<V> decoder;

		public Builder(@NotNull Class<V> type) {
			Objects.requireNonNull(type, "type is null");
			this.type = type;

			this.parameterCount = type.getTypeParameters().length;
			this.parameters = new ArrayList<>(this.parameterCount);

			for (int i = 0; i < this.parameterCount; i++)
				this.parameters.add(new GenericParameterInfo<>(this.type, i));
		}

		@NotNull
		public Builder<V> encoder(@NotNull GenericEncoder<V> encoder) {
			Objects.requireNonNull(encoder, "encoder is null");
			this.encoder = encoder;
			return this;
		}

		@NotNull
		public Builder<V> decoder(@NotNull GenericDecoder<V> decoder) {
			Objects.requireNonNull(decoder, "decoder is null");
			this.decoder = decoder;
			return this;
		}

		public Builder<V> parameter(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull Consumer<GenericParameterInfo.Builder<V>> callback) {
			Objects.checkIndex(index, this.parameterCount);
			Objects.requireNonNull(callback, "callback is null");

			GenericParameterInfo.Builder<V> builder = new GenericParameterInfo.Builder<>(this.type, index);
			callback.accept(builder);
			this.parameters.set(index, builder.build());

			return this;
		}

		@NotNull
		public Builder<V> parameter(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
			return this.parameter(index, GenericParameterInfo.Builder::ignore);
		}

		@NotNull
		public Builder<V> parameterResolver(@Range(from = 0, to = Integer.MAX_VALUE) int index, @NotNull TypeResolver<V> resolver) {
			return this.parameter(index, b -> b.resolver(resolver));
		}

		@NotNull
		public GenericInfo<V> build() {
			Objects.requireNonNull(this.encoder, "Require encoder");
			Objects.requireNonNull(this.decoder, "Require decoder");

			return new GenericInfo<>(this.type, this.parameters, this.encoder, this.decoder);
		}
	}
}
