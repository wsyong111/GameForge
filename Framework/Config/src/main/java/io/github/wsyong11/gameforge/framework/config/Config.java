package io.github.wsyong11.gameforge.framework.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Set;

public interface Config {
	@NotNull
	@UnmodifiableView
	Set<String> getKeys();

	boolean getBoolean(@NotNull String key);

	interface Editor {

	}
}
