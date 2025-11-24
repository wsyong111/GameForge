package io.github.wsyong11.gameforge.game.common.service;

import io.github.wsyong11.gameforge.framework.annotation.UnsafeAPI;
import org.jetbrains.annotations.Nullable;

public interface StubService<T> extends IService{
	@UnsafeAPI
	@Nullable
	T getDelegate();
}
