package io.github.wsyong11.gameforge.framework.i18n;

import io.github.wsyong11.gameforge.framework.i18n.pack.LanguagePack;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Locale;

public interface I18nProvider {
	@NotNull
	String get(@NotNull @NonNls String key);

	@NotNull
	String get(@NotNull @NonNls String key, Object... args);

	@NotNull
	Locale getLanguage();

	@NotNull
	@Unmodifiable
	List<LanguagePack> listLanguagePacks();
}
