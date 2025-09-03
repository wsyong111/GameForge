package io.github.wsyong11.gameforge.framework.event.reflect;

import io.github.wsyong11.gameforge.framework.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SubscribeEvent {
	@NotNull
	EventPriority priority() default EventPriority.NORMAL;
}
