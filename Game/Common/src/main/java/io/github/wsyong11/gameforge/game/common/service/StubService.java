package io.github.wsyong11.gameforge.game.common.service;

import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import org.jetbrains.annotations.Nullable;

public interface StubService<T> extends IService{
	@UnsafeAPI
	@Nullable
	T getDelegate();
}
