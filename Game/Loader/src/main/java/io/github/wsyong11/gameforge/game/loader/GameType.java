package io.github.wsyong11.gameforge.game.loader;

import io.github.wsyong11.gameforge.framework.app.Application;
import io.github.wsyong11.gameforge.game.common.core.StartupConfig;
import io.github.wsyong11.gameforge.game.core.client.ClientGame;
import io.github.wsyong11.gameforge.game.core.server.ServerGame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("Convert2MethodRef")
public enum GameType {
	// !! Do not replace it with the method ref, this will load class !!
	CLIENT("client", () -> config -> new ClientGame(config)),
	SERVER("server", () -> config -> new ServerGame(config));

	@Nullable
	public static GameType parse(@Nullable String id) {
		if (id == null)
			return null;

		for (GameType type : values()) {
			if (type.getId().equalsIgnoreCase(id)) return type;
		}
		return null;
	}

	private final String id;
	private final Supplier<Function<StartupConfig, Application>> factory;

	GameType(@NotNull String id, @NotNull Supplier<Function<StartupConfig, Application>> factory) {
		Objects.requireNonNull(id, "id is null");
		Objects.requireNonNull(factory, "factory is null");

		this.id = id;
		this.factory = factory;
	}

	@NotNull
	public String getId() {
		return this.id;
	}

	@NotNull
	public Supplier<Function<StartupConfig, Application>> getFactory() {
		return this.factory;
	}

	@Override
	public String toString() {
		return "GameType[\"" + this.id + "\"]";
	}
}
