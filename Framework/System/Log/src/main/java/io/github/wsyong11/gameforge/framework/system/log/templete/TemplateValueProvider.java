package io.github.wsyong11.gameforge.framework.system.log.templete;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@FunctionalInterface
public interface TemplateValueProvider extends Supplier<Object> {
	@Nullable
	Object getValue();

	@Override
	default Object get() {
		return this.getValue();
	}
}
