package io.github.wsyong11.gameforge.framework.event;

import io.github.wsyong11.gameforge.util.Nameable;
import org.apache.commons.lang3.ClassUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class Event implements Nameable {
	@SuppressWarnings("unchecked")
	public static List<Class<? extends Event>> getSuperClasses(@NotNull Class<? extends Event> type) {
		Objects.requireNonNull(type, "type is null");

		List<Class<?>> hierarchy = ClassUtils.getAllSuperclasses(type);
		hierarchy.remove(Object.class);
		hierarchy.add(0, type);

		return hierarchy
			.stream()
			.filter(Event.class::isAssignableFrom)
			.<Class<? extends Event>>map(clazz -> (Class<? extends Event>) clazz)
			.toList();
	}

	@NotNull
	@Override
	public String getName() {
		return this.getClass().getSimpleName() + "[" + this.hashCode() + "]";
	}
}
