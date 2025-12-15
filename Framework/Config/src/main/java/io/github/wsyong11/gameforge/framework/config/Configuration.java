package io.github.wsyong11.gameforge.framework.config;

import io.github.wsyong11.gameforge.framework.config.file.ConfigFile;
import io.github.wsyong11.gameforge.framework.config.preference.PreferenceStorage;
import org.jetbrains.annotations.NotNull;

import java.nio.channels.Channel;

public abstract class Configuration {
	public static <T extends Configuration> T create(@NotNull Class<T> type, @NotNull ConfigFile file){

	}
}
