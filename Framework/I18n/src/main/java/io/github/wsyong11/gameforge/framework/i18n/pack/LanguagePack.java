package io.github.wsyong11.gameforge.framework.i18n.pack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Locale;
import java.util.Set;

public interface LanguagePack {
	@NotNull
	String getName(@NotNull Locale locale);

	@NotNull
	Locale getLocale();

	@NotNull
	@UnmodifiableView
	Set<String> getKeys();

	@Nullable
	String getValue(@NotNull String key);
}
