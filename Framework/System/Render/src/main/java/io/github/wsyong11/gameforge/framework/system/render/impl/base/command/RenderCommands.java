package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.className;

@UtilityClass
public class RenderCommands {
	private static final Logger LOGGER = Log.getLogger();

	private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

	private static final ClassValue<Integer> TYPE_ID = new ClassValue<>() {
		@NotNull
		@Override
		protected Integer computeValue(@NotNull Class<?> type) {
			if (Modifier.isAbstract(type.getModifiers()))
				throw new IllegalArgumentException("Abstract class cannot compute id");

			int id = ID_COUNTER.getAndIncrement();
			if (LOGGER.isTraceEnabled())
				LOGGER.trace("Register command {} as id {}", className(type), id);

			return id;
		}
	};

	private static final ClassValue<Supplier<? extends RenderCommand>> FACTORY = new ClassValue<>() {
		@NotNull
		@Override
		protected Supplier<? extends RenderCommand> computeValue(Class<?> type) {
			Objects.requireNonNull(type, "type is null");

			if (!RenderCommand.class.isAssignableFrom(type))
				throw new IllegalArgumentException(type.getName() + " is not a subtype of " + RenderCommand.class.getName());

			return null;
		}
	}

	public static int getId(@NotNull Class<? extends RenderCommand> type) {
		Objects.requireNonNull(type, "type is null");
		return TYPE_ID.get(type);
	}

}
