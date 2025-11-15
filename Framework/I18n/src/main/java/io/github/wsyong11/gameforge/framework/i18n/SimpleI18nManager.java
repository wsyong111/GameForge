package io.github.wsyong11.gameforge.framework.i18n;

import io.github.wsyong11.gameforge.framework.i18n.listener.LanguageChangeListener;
import io.github.wsyong11.gameforge.framework.i18n.pack.LanguagePack;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.lazy;

public class SimpleI18nManager implements I18nManager {
	private static final Logger LOGGER = Log.getLogger();

	private final Map<Locale, LanguagePack> languagePackMap;
	private final ListenerList listenerList;

	@Unmodifiable
	private volatile Map<String, String> localeTexts;

	private final Locale defaultLanguage;
	private volatile Locale language;

	public SimpleI18nManager(@NotNull Locale defaultLanguage) {
		Objects.requireNonNull(defaultLanguage, "defaultLanguage is null");

		this.languagePackMap = new ConcurrentHashMap<>();
		this.listenerList = ListenerList.sync();

		this.localeTexts = Map.of();

		this.defaultLanguage = defaultLanguage;
		this.language = defaultLanguage;
	}

	@NotNull
	protected String format(@NotNull String text, @NotNull Object[] args) {
		Objects.requireNonNull(text, "text is null");
		Objects.requireNonNull(args, "args is null");
		return String.format(text, args);
	}

	@NotNull
	protected String getTranslate(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.localeTexts.getOrDefault(key, key);
	}

	@Override
	public void addChangeListener(@NotNull LanguageChangeListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(LanguageChangeListener.class, listener);
	}

	@Override
	public void removeChangeListener(@NotNull LanguageChangeListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(LanguageChangeListener.class, listener);
	}

	@NotNull
	@Override
	public Locale getLanguage() {
		return this.language;
	}

	@NotNull
	@Unmodifiable
	@Override
	public List<LanguagePack> listLanguagePacks() {
		return List.copyOf(this.languagePackMap.values());
	}

	@Override
	public void setLanguage(@NotNull Locale language) {
		Objects.requireNonNull(language, "language is null");

		if (Objects.equals(this.language, language))
			return;

		this.language = language;
		LOGGER.info("Language changed to {}", language);
	}

	@Override
	public void addLanguagePack(@NotNull LanguagePack pack) {
		Objects.requireNonNull(pack, "pack is null");

		Locale locale = pack.getLocale();
		LOGGER.debug("Add language pack: {}", locale);
		LanguagePack oldPack = this.languagePackMap.put(locale, pack);
		if (oldPack != null)
			LOGGER.warn("Override language pack: {} {} -> {}", locale, lazy(oldPack), lazy(pack));
	}

	@Override
	public boolean removeLanguagePack(@NotNull Locale locale) {
		Objects.requireNonNull(locale, "locale is null");

		boolean removed = this.languagePackMap.remove(locale) != null;
		if (removed)
			LOGGER.debug("Remove language pack: {}", locale);
		return removed;
	}


	// TODO: 2025/11/15 Locale fallback chain
	@Override
	public void reload() {
		LOGGER.debug("Reloading locale entry’s");

		LanguagePack defaultLanguagePack = this.languagePackMap.get(this.defaultLanguage);
		if (defaultLanguagePack == null)
			LOGGER.warn("Missing default language pack: {}", this.defaultLanguage);

		LanguagePack languagePack = this.languagePackMap.get(this.language);
		if (languagePack == null)
			LOGGER.debug("Missing language pack for current language {}", this.language);

		Map<String, String> localeTexts = new HashMap<>();

		if (defaultLanguagePack != null && languagePack != null) {
			Set<String> keys = defaultLanguagePack.getKeys();
			Set<String> localedKeys = languagePack.getKeys();
			for (String key : keys) {
				if (localedKeys.contains(key)) {
					localeTexts.put(key, languagePack.getValue(key));
				} else {
					LOGGER.warn("Missing the locale key {}", key);
					localeTexts.put(key, defaultLanguagePack.getValue(key));
				}
			}
		} else {
			LanguagePack availableLanguagePack = Objects.requireNonNullElse(languagePack, defaultLanguagePack);
			for (String key : availableLanguagePack.getKeys()) {
				String text = availableLanguagePack.getValue(key);
				if (text != null)
					localeTexts.put(key, text);
			}
		}

		LOGGER.debug("Total {} locale entry's", localeTexts.size());

		this.localeTexts = Collections.unmodifiableMap(localeTexts);

		this.listenerList.fire(LanguageChangeListener.class,
			l -> l.onLanguageChanged(this),
			ListenerExceptionCallback.log(LOGGER));
	}

	@NotNull
	@Override
	public String get(@NotNull String key) {
		Objects.requireNonNull(key, "key is null");
		return this.getTranslate(key);
	}

	@NotNull
	@Override
	public String get(@NotNull String key, Object... args) {
		Objects.requireNonNull(key, "key is null");
		return this.format(this.getTranslate(key), args);
	}
}
