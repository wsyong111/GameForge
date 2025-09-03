package io.github.wsyong11.gameforge.framework.dataflow.loader;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class SimpleDataLoader<T> extends DataLoader<T> {
	// FIX: Implicit class loading
	private final Map<Integer, Supplier<Supplier<Parser<T>>>> parserMap;
	private final Map<Integer, Parser<T>> parserInstances;
	private int maxVersion;

	public SimpleDataLoader() {
		this.parserMap = new ConcurrentHashMap<>();
		this.parserInstances = new ConcurrentHashMap<>();

		this.maxVersion = 0;
	}

	@Contract("_, _ -> this")
	public SimpleDataLoader<T> register(int version, @NotNull Supplier<Supplier<Parser<T>>> factory) {
		Objects.requireNonNull(factory, "factory is null");

		this.maxVersion = Math.max(this.maxVersion, version);
		this.parserMap.put(version, factory);
		return this;
	}

	@Nullable
	@Override
	protected Parser<T> getParser(int version) {
		int normalizedVersion = Math.min(version, this.maxVersion);

		return this.parserInstances.computeIfAbsent(normalizedVersion, k -> {
			Supplier<Supplier<Parser<T>>> factory = this.parserMap.get(normalizedVersion);
			return factory == null ? null : factory.get().get();
		});
	}

	@Override
	protected int getLatestVersion() {
		return this.maxVersion;
	}
}
