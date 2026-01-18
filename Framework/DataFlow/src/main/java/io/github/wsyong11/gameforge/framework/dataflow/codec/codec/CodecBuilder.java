package io.github.wsyong11.gameforge.framework.dataflow.codec.codec;

import io.github.wsyong11.gameforge.framework.dataflow.codec.CodecContext;
import io.github.wsyong11.gameforge.framework.dataflow.element.Element;
import io.github.wsyong11.gameforge.framework.ex.CodecException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class CodecBuilder<T> {
	private final Set<Class<?>> supportTypes;

	private Encoder<T> encoder;
	private Decoder<T> decoder;
	@Nullable
	private Predicate<Class<?>> supportTypePredicate;
	@Nullable
	private BiPredicate<Class<? extends T>, Object> supportValuePredicate;
	@Nullable
	private BiPredicate<Class<? extends T>, Element> supportElementPredicate;

	public CodecBuilder() {
		this.supportTypes = new HashSet<>();

		this.encoder = null;
		this.decoder = null;

		this.supportTypePredicate = null;
		this.supportValuePredicate = null;
		this.supportElementPredicate = null;
	}

	@NotNull
	public CodecBuilder<T> encoder(@NotNull Encoder<T> encoder) {
		Objects.requireNonNull(encoder, "encoder is null");
		this.encoder = encoder;
		return this;
	}

	@NotNull
	public CodecBuilder<T> decoder(@NotNull Decoder<T> decoder) {
		Objects.requireNonNull(decoder, "decoder is null");
		this.decoder = decoder;
		return this;
	}

	@NotNull
	public CodecBuilder<T> supportTypes(@NotNull Class<?>... type) {
		Objects.requireNonNull(type, "type is null");
		this.supportTypes.addAll(Set.of(type));
		return this;
	}

	@NotNull
	public CodecBuilder<T> supportType(@NotNull Class<?> type) {
		Objects.requireNonNull(type, "type is null");
		this.supportTypes.add(type);
	}

	@NotNull
	public CodecBuilder<T> supportTypePredicate(@Nullable Predicate<Class<?>> predicate) {
		this.supportTypePredicate = predicate;
		return this;
	}

	@NotNull
	public CodecBuilder<T> supportValue(@Nullable BiPredicate<Class<? extends T>, Object> predicate) {
		this.supportValuePredicate = predicate;
		return this;
	}

	@NotNull
	public CodecBuilder<T> supportValue(@Nullable Predicate<Object> predicate) {
		this.supportValuePredicate = predicate == null
			? null
			: (type, value) -> predicate.test(value);
		return this;
	}

	@NotNull
	public CodecBuilder<T> supportElement(@Nullable BiPredicate<Class<? extends T>, Element> predicate) {
		this.supportElementPredicate = predicate;
		return this;
	}

	@NotNull
	public CodecBuilder<T> supportElement(@Nullable Predicate<Element> predicate) {
		this.supportElementPredicate = predicate == null
			? null
			: (type, element) -> predicate.test(element);
		return this;
	}

	@NotNull
	public Codec<T> build() {
		Set<Class<?>> supportTypes = this.supportTypes.isEmpty() ? null : this.supportTypes;
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

	private static class CodecImpl<V> implements Codec<V> {
		private final Encoder<V> encoder;
		private final Decoder<V> decoder;
		private final
		private final Predicate<Class<?>> supportTypePredicate;
		@Nullable
		private final BiPredicate<Class<? extends V>, Object> supportValuePredicate;
		@Nullable
		private final BiPredicate<Class<? extends V>, Element> supportElementPredicate;

		@Override
		public @NotNull Element encode(@NotNull CodecContext ctx, @NotNull V value, @NotNull Class<? extends V> type) throws CodecException {
			return null;
		}

		@Override
		public @NotNull V decode(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends V> type) throws CodecException {
			return null;
		}

		@Override
		public boolean isSupportType(@NotNull Class<?> type) {
			return false;
		}

		@Override
		public @Nullable Set<Class<?>> getSupportTypes() {
			return Codec.super.getSupportTypes();
		}

		@Override
		public boolean isSupportValue(@NotNull CodecContext ctx, @NotNull Object value, @NotNull Class<? extends V> type) {
			return false;
		}

		@Override
		public boolean isSupportElement(@NotNull CodecContext ctx, @NotNull Element element, @NotNull Class<? extends V> type) {
			return false;
		}
	}
}
