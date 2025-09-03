package io.github.wsyong11.gameforge.framework.event.reflect;

import io.github.wsyong11.gameforge.framework.event.Event;
import io.github.wsyong11.gameforge.framework.event.EventPriority;
import io.github.wsyong11.gameforge.framework.event.IEventListener;
import io.github.wsyong11.gameforge.framework.event.ex.ListenerException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.util.reflect.ReflectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class ReflectEventListener implements IEventListener<Event> {
	private static final Logger LOGGER = Log.getLogger();

	@NotNull
	private static List<Method> scanMethods(@NotNull Class<?> clazz, boolean staticMethod) {
		Objects.requireNonNull(clazz, "clazz is null");

		return Arrays
			.stream(clazz.getDeclaredMethods())
			.filter(method -> {
				int modifiers = method.getModifiers();
				return !Modifier.isAbstract(modifiers)
					&& (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers))
					&& (Modifier.isStatic(modifiers) == staticMethod);
			})
			.toList();
	}

	@NotNull
	public static Map<Class<? extends Event>, List<ReflectEventListener>> scanStaticMethod(@NotNull Class<?> clazz) {
		Objects.requireNonNull(clazz, "clazz is null");
		return createListeners(clazz, null, scanMethods(clazz, true));
	}

	@NotNull
	public static Map<Class<? extends Event>, List<ReflectEventListener>> scanMethod(@NotNull Class<?> clazz, @NotNull Object instance) {
		Objects.requireNonNull(clazz, "clazz is null");
		Objects.requireNonNull(instance, "instance is null");
		return createListeners(clazz, instance, scanMethods(clazz, false));
	}

	@NotNull
	private static Map<Class<? extends Event>, List<ReflectEventListener>> createListeners(@NotNull Class<?> clazz, @Nullable Object instance, @NotNull List<Method> methods) {
		Objects.requireNonNull(clazz, "clazz is null");
		Objects.requireNonNull(methods, "methods is null");

		Map<Class<? extends Event>, List<ReflectEventListener>> listeners = new HashMap<>();
		for (Method method : methods) {
			SubscribeEvent subscribeEvent = method.getAnnotation(SubscribeEvent.class);
			if (subscribeEvent == null)
				continue;

			Class<?>[] parameters = method.getParameterTypes();
			if (parameters.length != 1) {
				LOGGER.warn("Method {} has {} parameters, but event listener method requires a single argument only",
					ReflectUtils.methodToString(method), parameters.length);
				continue;
			}

			Class<?> rawEventType = parameters[0];
			if (!Event.class.isAssignableFrom(rawEventType)) {
				LOGGER.warn("Method {} parameter type is not a subclass of Event",
					ReflectUtils.methodToString(method));
				continue;
			}

			@SuppressWarnings("unchecked")
			Class<? extends Event> eventType = (Class<? extends Event>) rawEventType;

			method.setAccessible(true);
			listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
			         .add(create(eventType, subscribeEvent.priority(), method, instance));
		}

		return listeners;
	}

	@NotNull
	private static ReflectEventListener create(@NotNull Class<? extends Event> eventType, @NotNull EventPriority priority, @NotNull Method method, @Nullable Object instance) {
		Objects.requireNonNull(eventType, "eventType is null");
		Objects.requireNonNull(priority, "priority is null");
		Objects.requireNonNull(method, "method is null");

		LOGGER.debug("Create reflect event listener from method {}, priority {}", ReflectUtils.methodToString(method), priority);
		return new MethodEventListener(eventType, priority, method, instance);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	private final Class<? extends Event> eventType;
	private final EventPriority priority;

	protected ReflectEventListener(@NotNull Class<? extends Event> eventType, @NotNull EventPriority priority) {
		Objects.requireNonNull(eventType, "eventType is null");
		Objects.requireNonNull(priority, "priority is null");
		this.eventType = eventType;
		this.priority = priority;
	}

	protected abstract void processEvent(@NotNull Event event);

	@Override
	public void onEvent(@NotNull Event event) {
		if (!this.eventType.isInstance(event))
			return;
		this.processEvent(event);
	}

	@NotNull
	public EventPriority getPriority() {
		return this.priority;
	}

	@NotNull
	public Class<? extends Event> getEventType() {
		return this.eventType;
	}

	private static class MethodEventListener extends ReflectEventListener {
		private final Method method;
		private final Object instance;

		public MethodEventListener(@NotNull Class<? extends Event> eventType, @NotNull EventPriority priority, @NotNull Method method, @Nullable Object instance) {
			super(eventType, priority);
			Objects.requireNonNull(method, "method is null");
			this.method = method;
			this.instance = instance;
		}

		@Override
		protected void processEvent(@NotNull Event event) {
			// Need to throw the error to trigger the error handler

			try {
				this.method.invoke(this.instance, event);
			} catch (IllegalAccessException e) {
				String name = ReflectUtils.methodToString(this.method);
				LOGGER.error("Cannot invoke event listener method {}", name, e);
				throw new ListenerException("Cannot invoke method " + name, e);
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				if (cause instanceof ListenerException re)
					throw re;

				throw new ListenerException("Reflect listener exception", cause);
			}
		}

		@Override
		public String toString() {
			return "ReflectEventListener[" + this.getEventType().getName() + ", "
				+ ReflectUtils.methodToString(this.method) + ", "
				+ this.getPriority() + "]"
				+ (this.instance != null ? " with " + this.instance : "");
		}
	}
}
