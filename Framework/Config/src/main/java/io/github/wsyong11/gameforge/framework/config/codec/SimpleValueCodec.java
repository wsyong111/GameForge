package io.github.wsyong11.gameforge.framework.config.codec;

import io.github.wsyong11.gameforge.framework.config.ex.ValueCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class SimpleValueCodec<T> implements ValueCodec<T> {
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
	private final Predicate<Class<? extends T>> supportTypePredicate;
	@Nullable
	private final BiPredicate<Class<? extends T>, T> supportValuePredicate;
	@Nullable
	private final BiPredicate<Class<? extends T>, Element> supportElementPredicate;

	public SimpleValueCodec(
		@NotNull Encoder<T> encoder,
		@NotNull Decoder<T> decoder,
		@NotNull Predicate<Class<? extends T>> supportTypePredicate
	) {
		this(encoder, decoder, supportTypePredicate, null, null);
	}

	public SimpleValueCodec(
		@NotNull Encoder<T> encoder,
		@NotNull Decoder<T> decoder,
		@NotNull Predicate<Class<? extends T>> supportTypePredicate,
		@Nullable BiPredicate<Class<? extends T>, T> supportValuePredicate,
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
	public Element encode(@NotNull CodecContext ctx, @NotNull T value, @NotNull Class<? extends T> type) throws ValueCodecException {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(value, "value is null");
		Objects.requireNonNull(type, "type is null");
		return this.encoder.encode(ctx, value, type);
	}

	@NotNull
	@Override
	public T decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends T> type) throws ValueCodecException {
		Objects.requireNonNull(ctx, "ctx is null");
		Objects.requireNonNull(element, "element is null");
		Objects.requireNonNull(type, "type is null");
		return this.decoder.decode(ctx, element, type);
	}

	@Override
	public boolean isSupportType(@NotNull Class<? extends T> type) {
		Objects.requireNonNull(type, "type is null");
		return this.supportTypePredicate.test(type);
	}

	@Override
	public boolean isSupportValue(@NotNull Class<? extends T> type, @NotNull T value) {
		Objects.requireNonNull(value, "value is null");
		return this.supportValuePredicate == null || this.supportValuePredicate.test(type, value);
	}

	@Override
	public boolean isSupportElement(@NotNull Class<? extends T> type, @NotNull Element value) {
		Objects.requireNonNull(value, "value is null");
		return this.supportElementPredicate == null || this.supportElementPredicate.test(type, value);
	}

	@FunctionalInterface
	public interface Encoder<V> {
		@NotNull
		Element encode(@NotNull CodecContext ctx, @NotNull V value, @NotNull Class<? extends V> type) throws ValueCodecException;
	}

	@FunctionalInterface
	public interface Decoder<V> {
		@NotNull
		V decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends V> type) throws ValueCodecException;
	}

	public static class Builder<T> {
		private Encoder<T> encoder = null;
		private Decoder<T> decoder = null;
		private Predicate<Class<? extends T>> supportTypePredicate = null;
		@Nullable
		private BiPredicate<Class<? extends T>, T> supportValuePredicate = null;
		@Nullable
		private BiPredicate<Class<? extends T>, Element> supportElementPredicate = null;

		@NotNull
		public Builder<T> encoder(@NotNull Encoder<T> encoder) {
			Objects.requireNonNull(encoder, "encoder is null");
			this.encoder = encoder;
			return this;
		}

		@NotNull
		public Builder<T> decoder(@NotNull Decoder<T> decoder) {
			Objects.requireNonNull(decoder, "decoder is null");
			this.decoder = decoder;
			return this;
		}

		@SafeVarargs
		@NotNull
		public final Builder<T> supportTypes(@NotNull Class<? extends T>... types) {
			Objects.requireNonNull(types, "types is null");
			return this.supportTypes(Set.of(types));
		}

		@NotNull
		public Builder<T> supportTypes(@NotNull Collection<Class<? extends T>> types) {
			Objects.requireNonNull(types, "types is null");
			return this.supportType(Set.copyOf(types)::contains);
		}

		@NotNull
		public Builder<T> supportType(@NotNull Predicate<Class<? extends T>> predicate) {
			Objects.requireNonNull(predicate, "predicate is null");
			this.supportTypePredicate = predicate;
			return this;
		}

		@NotNull
		public Builder<T> supportValue(@Nullable BiPredicate<Class<? extends T>, T> predicate) {
			this.supportValuePredicate = predicate;
			return this;
		}

		@NotNull
		public Builder<T> supportValue(@Nullable Predicate<T> predicate) {
			this.supportValuePredicate = predicate == null
				? null
				: (type, value) -> predicate.test(value);
			return this;
		}

		@NotNull
		public Builder<T> supportElement(@Nullable BiPredicate<Class<? extends T>, Element> predicate) {
			this.supportElementPredicate = predicate;
			return this;
		}

		@NotNull
		public Builder<T> supportElement(@Nullable Predicate<Element> predicate) {
			this.supportElementPredicate = predicate == null
				? null
				: (type, element) -> predicate.test(element);
			return this;
		}

		@NotNull
		public SimpleValueCodec<T> build() {
			Objects.requireNonNull(this.encoder, "Require encoder");
			Objects.requireNonNull(this.decoder, "Require decoder");
			Objects.requireNonNull(this.supportTypePredicate, "Require support type predicate");
			return new SimpleValueCodec<>(
				this.encoder,
				this.decoder,
				this.supportTypePredicate,
				this.supportValuePredicate,
				this.supportElementPredicate
			);
		}
	}
}
