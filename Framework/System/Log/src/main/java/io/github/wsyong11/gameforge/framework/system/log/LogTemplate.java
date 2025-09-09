package io.github.wsyong11.gameforge.framework.system.log;

import io.github.wsyong11.gameforge.framework.system.log.templete.TemplateValueProvider;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class LogTemplate {
	@NotNull
	public static TemplateValueProvider lazy(@NotNull TemplateValueProvider supplier) {
		Objects.requireNonNull(supplier, "supplier is null");
		return supplier;
	}
}
