package io.github.wsyong11.gameforge.game.client.service;

import io.github.wsyong11.gameforge.framework.i18n.I18nManager;
import io.github.wsyong11.gameforge.framework.i18n.I18nProvider;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.game.common.service.StubService;
import org.jetbrains.annotations.NotNull;

public interface I18nService extends StubService<I18nManager>, I18nProvider {
	ResourcePath LANG_LOCATION = ResourcePath.of("lang");

	@Override
	@NotNull
	default String getServiceName() {
		return I18nService.class.getName();
	}
}
