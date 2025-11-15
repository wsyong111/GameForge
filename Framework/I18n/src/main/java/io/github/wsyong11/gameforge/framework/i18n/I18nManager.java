package io.github.wsyong11.gameforge.framework.i18n;

import io.github.wsyong11.gameforge.framework.i18n.listener.LanguageChangeListener;
import io.github.wsyong11.gameforge.framework.i18n.pack.LanguagePack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public interface I18nManager extends I18nProvider {
	void addChangeListener(@NotNull LanguageChangeListener listener);

	void removeChangeListener(@NotNull LanguageChangeListener listener);

	void setLanguage(@NotNull Locale language);

	void addLanguagePack(@NotNull LanguagePack pack);

	boolean removeLanguagePack(@NotNull Locale locale);

	void reload();
}
