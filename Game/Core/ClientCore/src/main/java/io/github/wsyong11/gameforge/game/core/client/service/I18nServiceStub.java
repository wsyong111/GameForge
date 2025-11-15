package io.github.wsyong11.gameforge.game.core.client.service;

import io.github.wsyong11.gameforge.framework.i18n.I18nManager;
import io.github.wsyong11.gameforge.framework.i18n.pack.LanguagePack;
import io.github.wsyong11.gameforge.game.client.service.I18nService;
import io.github.wsyong11.gameforge.util.Wrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class I18nServiceStub extends Wrapper<I18nManager> implements I18nService {
	public I18nServiceStub(@NotNull I18nManager manager) {
		super(Objects.requireNonNull(manager, "manager is null"));
	}

	@NotNull
	@Override
	public String get(@NotNull String key) {
		return this.delegate().get(key);
	}

	@NotNull
	@Override
	public String get(@NotNull String key, Object... args) {
		return this.delegate().get(key, args);
	}

	@NotNull
	@Override
	public Locale getLanguage() {
		return this.delegate().getLanguage();
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<LanguagePack> listLanguagePacks() {
		return this.delegate().listLanguagePacks();
	}

	@Nullable
	@Override
	public I18nManager getDelegate() {
		return super.getDelegate();
	}
}
