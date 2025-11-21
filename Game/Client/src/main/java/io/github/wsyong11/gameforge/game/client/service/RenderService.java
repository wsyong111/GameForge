package io.github.wsyong11.gameforge.game.client.service;

import io.github.wsyong11.gameforge.game.common.service.IService;
import org.jetbrains.annotations.NotNull;

public interface RenderService extends IService {
	@Override
	@NotNull
	default String getServiceName() {
		return RenderService.class.getName();
	}
}
