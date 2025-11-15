package io.github.wsyong11.gameforge.framework.i18n.pack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SimpleLanguagePack implements LanguagePack {
	private final Locale locale;
	private final Map<String, String> texts;

	public SimpleLanguagePack(@NotNull Locale locale, @NotNull Map<String, String> texts) {
		Objects.requireNonNull(locale, "locale is null");

		this.locale = locale;
		this.texts = Map.copyOf(texts);
	}

	@NotNull
	@Override
	public String getName(@NotNull Locale locale) {
		Objects.requireNonNull(locale, "locale is null");
		return this.locale.getDisplayName(locale);
	}

	@NotNull
	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@NotNull
	@UnmodifiableView
	@Override
	public Set<String> getKeys() {
		return this.texts.keySet();
	}

	@Nullable
	@Override
	public String getValue(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.texts.get(key);
	}
}
