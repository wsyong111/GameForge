package io.github.wsyong11.gameforge.framework.dataflow.codec.codec;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class SimpleCodec<T> implements Codec<T> {
	@NotNull
	public static <V> Builder<V> builder(@NotNull Class<V> ignoredType) {
		return new Builder<>();
	}

	@NotNull
	public static <V> Builder<V> builder() {
		return new Builder<>();
	}

	private final Encoder<T> encoder;
	private final Decoder<T> decoder;
	private final Predicate<Class<?>> supportTypePredicate;
	@Nullable
	private final BiPredicate<Class<? extends T>, Object> supportValuePredicate;
	@Nullable
	private final BiPredicate<Class<? extends T>, Element> supportElementPredicate;

	public SimpleCodec(
		@NotNull Encoder<T> encoder,
		@NotNull Decoder<T> decoder,
		@NotNull Predicate<Class<?>> supportTypePredicate
	) {
		this(encoder, decoder, supportTypePredicate, null, null);
	}

	public SimpleCodec(
		@NotNull Encoder<T> encoder,
		@NotNull Decoder<T> decoder,
		@NotNull Predicate<Class<?>> supportTypePredicate,
		@Nullable BiPredicate<Class<? extends T>, Object> supportValuePredicate,
		@Nullable BiPredicate<Class<? extends T>, Element> supportElementPredicate
	) {
		Objects.requireNonNull(encoder, "encoder is null");
		Objects.requireNonNull(decoder, "decoder is null");
		Objects.requireNonNull(supportTypePredicate, "supportTypePredicate is null");

		this.encoder = encoder;
		this.decoder = decoder;
		this.supportTypePredicate = supportTypePredicate;
		this.supportValuePredicate = supportValuePredicate;
		this.supportElementPredicate = supportElementPredicate;
	}

	@NotNull
	@Override
	public Element encode(@NotNull CodecContext ctx, @NotNull T value, @NotNull Class<? extends T> type) throws CodecException {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(value, "value is null");
		Objects.requireNonNull(type, "type is null");
		return this.encoder.encode(ctx, value, type);
	}

	@NotNull
	@Override
	public T decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends T> type) throws CodecException {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(type, "type is null");
		return this.decoder.decode(ctx, element, type);
	}

	@Override
	public boolean isSupportType(@NotNull Class<?> type) {
		Objects.requireNonNull(type, "type is null");
		return this.supportTypePredicate.test(type);
	}

	@Override
	public boolean isSupportValue(@NotNull CodecContext ctx, @NotNull Object value, @NotNull Class<? extends T> type) {
		Objects.requireNonNull(value, "value is null");
		Objects.requireNonNull(type, "type is null");
		return this.supportValuePredicate == null || this.supportValuePredicate.test(type, value);
	}

	@Override
	public boolean isSupportElement(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends T> type) {
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(type, "type is null");
		return this.supportElementPredicate == null || this.supportElementPredicate.test(type, element);
	}

	@FunctionalInterface
	public interface Encoder<V> {
		@NotNull
		Element encode(@NotNull CodecContext ctx, @NotNull V value, @NotNull Class<? extends V> type) throws CodecException;
	}

	@FunctionalInterface
	public interface Decoder<V> {
		@NotNull
		V decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends V> type) throws CodecException;
	}

	public static class Builder<V> {
		private Encoder<V> encoder = null;
		private Decoder<V> decoder = null;
		private Predicate<Class<?>> supportTypePredicate = null;
		@Nullable
		private BiPredicate<Class<? extends V>, Object> supportValuePredicate = null;
		@Nullable
		private BiPredicate<Class<? extends V>, Element> supportElementPredicate = null;

		@NotNull
		public Builder<V> encoder(@NotNull Encoder<V> encoder) {
			Objects.requireNonNull(encoder, "encoder is null");
			this.encoder = encoder;
			return this;
		}

		@NotNull
		public Builder<V> decoder(@NotNull Decoder<V> decoder) {
			Objects.requireNonNull(decoder, "decoder is null");
			this.decoder = decoder;
			return this;
		}

		@SafeVarargs
		@NotNull
		public final Builder<V> supportTypes(@NotNull Class<? extends V>... types) {
			Objects.requireNonNull(types, "types is null");
			return this.supportTypes(Set.of(types));
		}

		@NotNull
		public Builder<V> supportTypes(@NotNull Collection<Class<?>> types) {
			Objects.requireNonNull(types, "types is null");
			return this.supportType(Set.copyOf(types)::contains);
		}

		@NotNull
		public Builder<V> supportType(@NotNull Predicate<Class<?>> predicate) {
			Objects.requireNonNull(predicate, "predicate is null");
			this.supportTypePredicate = predicate;
			return this;
		}

		@NotNull
		public Builder<V> supportValue(@Nullable BiPredicate<Class<? extends V>, Object> predicate) {
			this.supportValuePredicate = predicate;
			return this;
		}

		@NotNull
		public Builder<V> supportValue(@Nullable Predicate<Object> predicate) {
			this.supportValuePredicate = predicate == null
				? null
				: (type, value) -> predicate.test(value);
			return this;
		}

		@NotNull
		public Builder<V> supportElement(@Nullable BiPredicate<Class<? extends V>, Element> predicate) {
			this.supportElementPredicate = predicate;
			return this;
		}

		@NotNull
		public Builder<V> supportElement(@Nullable Predicate<Element> predicate) {
			this.supportElementPredicate = predicate == null
				? null
				: (type, element) -> predicate.test(element);
			return this;
		}

		@NotNull
		public SimpleCodec<V> build() {
			Objects.requireNonNull(this.encoder, "Require encoder");
			Objects.requireNonNull(this.decoder, "Require decoder");
			Objects.requireNonNull(this.supportTypePredicate, "Require support type predicate");
			return new SimpleCodec<>(
				this.encoder,
				this.decoder,
				this.supportTypePredicate,
				this.supportValuePredicate,
				this.supportElementPredicate
			);
		}
	}
}
