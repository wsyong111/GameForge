package io.github.wsyong11.gameforge.framework.system.render.impl.base.command;

import io.github.wsyong11.gameforge.framework.ex.RuntimeReflectException;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.*;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static io.github.wsyong11.gameforge.framework.system.log.LogTemplate.className;

@UtilityClass
public class RenderCommands {
	private static final Logger LOGGER = Log.getLogger();

	private static final Map<Class<?>, Integer> COMMAND_ID_MAP = new ConcurrentHashMap<>();

	private static final AtomicInteger ID_COUNTER = new AtomicInteger(0);

	private static final ClassValue<Integer> TYPE_ID = new ClassValue<>() {
		@NotNull
		@Override
		protected Integer computeValue(@NotNull Class<?> type) {
			if (Modifier.isAbstract(type.getModifiers()))
				throw new IllegalArgumentException("Abstract class cannot compute id");

			return COMMAND_ID_MAP.computeIfAbsent(type, (k) -> {
				int id = ID_COUNTER.getAndIncrement();
				if (LOGGER.isTraceEnabled())
					LOGGER.trace("Register command {} as id {}", className(type), id);
				return id;
			});
		}
	};

	private static final ClassValue<Supplier<? extends RenderCommand>> FACTORY = new ClassValue<>() {
		@SuppressWarnings("unchecked")
		@NotNull
		@Override
		protected Supplier<? extends RenderCommand> computeValue(@NotNull Class<?> type) {
			Objects.requireNonNull(type, "type is null");

			if (!RenderCommand.class.isAssignableFrom(type))
				throw new IllegalArgumentException(type.getName() + " is not a subtype of " + RenderCommand.class.getName());

			if (Modifier.isAbstract(type.getModifiers()))
				throw new IllegalArgumentException(type.getName() + " is abstract class");

			MethodHandles.Lookup lookup = MethodHandles.lookup();
			CallSite callSite;
			try {
				callSite = LambdaMetafactory.metafactory(
					lookup,
					"get",
					MethodType.methodType(Supplier.class),
					MethodType.methodType(Object.class),
					lookup.findConstructor(type, MethodType.methodType(void.class)),
					MethodType.methodType(type)
				);
			} catch (LambdaConversionException | NoSuchMethodException | IllegalAccessException e) {
				LOGGER.error("Cannot find constructor call site from command {}", className(type), e);
				throw new RuntimeReflectException("Exception when finding constructor call site", e);
			}

			Supplier<? extends RenderCommand> factory;
			try {
				factory = (Supplier<? extends RenderCommand>) callSite.getTarget().invoke();
			} catch (Throwable e) {
				LOGGER.error("Failed to create constructor factory from command {}", className(type), e);
				throw new RuntimeReflectException("Exception when creating constructor factory", e);
			}

			return factory;
		}
	};

	public static int getId(@NotNull Class<? extends RenderCommand> type) {
		Objects.requireNonNull(type, "type is null");
		return TYPE_ID.get(type);
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public static <T extends RenderCommand> Supplier<T> getFactory(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");
		return (Supplier<T>) FACTORY.get(type);
	}
}
