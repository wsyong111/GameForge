package io.github.wsyong11.gameforge.framework.env;

import io.github.wsyong11.gameforge.framework.context.annotation.UsingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class ConfigValueMapper<T, E> implements Supplier<T> {
	@NotNull
	public static <EV> Builder<?, EV> create(@NotNull EnvConfig.Entry<EV> entry) {
		Objects.requireNonNull(entry, "entry is null");
		return new Builder<>(entry);
	}

	private final EnvConfig.Entry<E> entry;
	@Nullable
	private final E defaultValue;
	private final Map<E, Supplier<T>> valueProviders;
	@Nullable
	private final Supplier<T> nullValueProvider;
	@Nullable
	private final Supplier<T> defaultValueProvider;

	public ConfigValueMapper(
		@NotNull EnvConfig.Entry<E> entry,
		@Nullable E defaultValue,
		@NotNull Map<E, Supplier<T>> valueProviders,
		@Nullable Supplier<T> nullValueProvider,
		@Nullable Supplier<T> defaultValueProvider
	) {
		Objects.requireNonNull(entry, "entry is null");
		Objects.requireNonNull(valueProviders, "valueProviders is null");

		this.entry = entry;
		this.defaultValue = defaultValue;
		this.valueProviders = Map.copyOf(valueProviders);
		this.nullValueProvider = nullValueProvider;
		this.defaultValueProvider = defaultValueProvider;
	}

	@UsingContext
	@Override
	public T get() {
		E value = EnvConfig.get(this.entry, this.defaultValue);

		Supplier<T> provider = value == null
			? this.nullValueProvider
			: this.valueProviders.get(value);

		if (provider == null)
			provider = this.defaultValueProvider;

		if (provider == null)
			throw new IllegalStateException("Value provider not defined: " + value);

		return provider.get();
	}

	public static class Builder<BT, BE> {
		private final EnvConfig.Entry<BE> entry;

		private BE defaultValue;

		private final Map<BE, Supplier<BT>> providers;
		private Supplier<BT> nullValueProvider;
		private Supplier<BT> defaultValueProvider;

		public Builder(@NotNull EnvConfig.Entry<BE> entry) {
			Objects.requireNonNull(entry, "entry is null");
			this.entry = entry;

			this.defaultValue = null;

			this.providers = new HashMap<>();
			this.nullValueProvider = null;
			this.defaultValueProvider = null;
		}

		@SuppressWarnings("unchecked")
		@NotNull
		public <T> Builder<T, BE> as() {
			return (Builder<T, BE>) this;
		}

		@NotNull
		public <T> Builder<T, BE> as(@NotNull Class<T> ignoredType) {
			return this.as();
		}

		@NotNull
		public Builder<BT, BE> when(@Nullable BE value, @NotNull Supplier<BT> provider) {
			Objects.requireNonNull(provider, "provider is null");

			if (value == null) {
				this.nullValueProvider = provider;
				return this;
			}

			this.providers.put(value, provider);

			return this;
		}

		@NotNull
		public Builder<BT, BE> whenDefault(@NotNull Supplier<BT> provider) {
			Objects.requireNonNull(provider, "provider is null");
			this.defaultValueProvider = provider;
			return this;
		}

		@NotNull
		public Builder<BT, BE> defaultValue(@Nullable BE value) {
			this.defaultValue = value;
			return this;
		}

		@NotNull
		public ConfigValueMapper<BT, BE> build() {
			return new ConfigValueMapper<>(
				this.entry,
				this.defaultValue,
				this.providers,
				this.nullValueProvider,
				this.defaultValueProvider
			);
		}
	}
}
