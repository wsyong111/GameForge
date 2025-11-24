package io.github.wsyong11.gameforge.framework.system.input.listener;

import io.github.wsyong11.gameforge.framework.key.InputKey;
import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.ModifyKey;
import io.github.wsyong11.gameforge.framework.listener.IListener;
import org.jetbrains.annotations.NotNull;

public interface KeyListener extends IListener {
	void onKeyInput(@NotNull InputKey key, @NotNull KeyAction action, @ModifyKey.Mask int mods);
}
