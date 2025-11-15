package io.github.wsyong11.gameforge.framework.i18n.listener;

import io.github.wsyong11.gameforge.framework.i18n.I18nProvider;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;

public interface LanguageChangeListener extends IListener {
	void onLanguageChanged(@NotNull I18nProvider i18n);
}
