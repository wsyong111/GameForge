package io.github.wsyong11.gameforge.framework.system.input.impl;

import io.github.wsyong11.gameforge.framework.key.KeyAction;
import io.github.wsyong11.gameforge.framework.key.KeyCode;
import io.github.wsyong11.gameforge.framework.key.ModifyKey;
import io.github.wsyong11.gameforge.framework.key.MouseButton;
import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.framework.listener.ex.ListenerExceptionCallback;
import io.github.wsyong11.gameforge.framework.system.input.ProcessInputManager;
import io.github.wsyong11.gameforge.framework.system.input.listener.KeyListener;
import io.github.wsyong11.gameforge.framework.system.input.listener.MouseListener;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.joml.Vector2d;
import org.joml.Vector2dc;

import java.util.*;

public class DefaultInputManagerOld implements ProcessInputManager {
	private static final Logger LOGGER = Log.getLogger();

	private static boolean isNormalActionChange(@NotNull KeyAction action, @NotNull KeyAction oldAction) {
		Objects.requireNonNull(action, "action is null");
		Objects.requireNonNull(oldAction, "oldAction is null");

		return (oldAction == KeyAction.UP && action == KeyAction.DOWN)
			|| (oldAction == KeyAction.UP && action == KeyAction.HOLD)
			|| (oldAction == KeyAction.DOWN && action == KeyAction.HOLD)
			|| (oldAction == KeyAction.DOWN && action == KeyAction.UP)
			|| (oldAction == KeyAction.HOLD && action == KeyAction.UP);
	}

	private final Map<KeyCode, KeyAction> keyMaps;
	private final Set<KeyCode> activeKeys;

	private final Set<MouseButton> activeMouseButtons;
	private final Vector2d mousePosition;

	private final ListenerList listenerList;

	public DefaultInputManagerOld() {
		this.keyMaps = new EnumMap<>(KeyCode.class);
		this.activeKeys = EnumSet.noneOf(KeyCode.class);

		this.activeMouseButtons = EnumSet.noneOf(MouseButton.class);
		this.mousePosition = new Vector2d(0D);

		this.listenerList = ListenerList.sync();
	}

	@Override
	public void processKeyInput(@NotNull KeyCode code, @NotNull KeyAction action, @ModifyKey.Mask int mods) {
		Objects.requireNonNull(code, "code is null");
		Objects.requireNonNull(action, "action is null");

		if (!ModifyKey.isValid(mods))
			throw new IllegalArgumentException("Unknown modify key flags: " + ModifyKey.toString(mods));

		KeyAction oldAction = this.keyMaps.put(code, action);
		if (oldAction==action)
			return;

		LOGGER.trace("Process key: {} {}", code, action);

		if (action.isPressed())
			this.activeKeys.add(code);
		else
			this.activeKeys.remove(code);
		if (oldAction != null && !isNormalActionChange(action, oldAction))
			LOGGER.debug("Key status is abnormal: {} {} -> {}", code, oldAction, action);

		this.listenerList.fire(
			KeyListener.class,
			l -> l.onKeyInput(code, action, mods),
			ListenerExceptionCallback.log(LOGGER));
	}

	@Override
	public void processMouseInput(@NotNull MouseButton button, @NotNull KeyAction action) {
		Objects.requireNonNull(button, "button is null");
		Objects.requireNonNull(action, "action is null");

		LOGGER.trace("Process mouse key: {} {}", button, action);

		if (action == KeyAction.DOWN)
			this.activeMouseButtons.add(button);
		else if (action == KeyAction.UP)
			this.activeMouseButtons.remove(button);

		this.listenerList.fire(
			MouseListener.class,
			l -> l.onMouseClick(button, action, this.mousePosition),
			ListenerExceptionCallback.log(LOGGER));
	}

	@Override
	public void processMouseMove(double x, double y) {
		this.mousePosition.set(x, y);

		this.listenerList.fire(
			MouseListener.class,
			l -> l.onMouseMove(this.mousePosition),
			ListenerExceptionCallback.log(LOGGER));
	}

	@NotNull
	@UnmodifiableView
	@Override
	public Set<KeyCode> getActiveKeys() {
		return Collections.unmodifiableSet(this.activeKeys);
	}

	@Override
	public boolean isPressed(@NotNull KeyCode code) {
		Objects.requireNonNull(code, "code is null");
		return this.getKeyAction(code).isPressed();
	}

	@NotNull
	@Override
	public KeyAction getKeyAction(@NotNull KeyCode code) {
		Objects.requireNonNull(code, "code is null");
		return this.keyMaps.getOrDefault(code, KeyAction.UP);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	@UnmodifiableView
	@Override
	public Set<MouseButton> getActiveMouseButtons() {
		return Collections.unmodifiableSet(this.activeMouseButtons);
	}

	@Override
	public boolean isPressed(@NotNull MouseButton button) {
		Objects.requireNonNull(button, "button is null");
		return this.activeMouseButtons.contains(button);
	}

	@NotNull
	@Override
	public KeyAction getMouseAction(@NotNull MouseButton button) {
		Objects.requireNonNull(button, "button is null");
		return this.isPressed(button) ? KeyAction.DOWN : KeyAction.UP;
	}

	@NotNull
	@Override
	public Vector2dc getMousePosition() {
		return this.mousePosition;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public void registerMouseListener(@NotNull MouseListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(MouseListener.class, listener);
	}

	@Override
	public void unregisterMouseListener(@NotNull MouseListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(MouseListener.class, listener);
	}

	@Override
	public void registerKeyListener(@NotNull KeyListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.add(KeyListener.class, listener);

	}

	@Override
	public void unregisterKeyListener(@NotNull KeyListener listener) {
		Objects.requireNonNull(listener, "listener is null");
		this.listenerList.remove(KeyListener.class, listener);
	}
}
