package io.github.wsyong11.gameforge.framework.system.log.templete;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@UtilityClass
public class Template {
	public static void process(Object @NotNull [] args) {
		Objects.requireNonNull(args, "args is null");

		if (args.length == 0)
			return;

		for (int i = 0; i < args.length; i++) {
			Object arg = args[i];
			if (arg instanceof TemplateValueProvider provider) {
				try {
					args[i] = provider.getValue();
				} catch (Exception e) {
					args[i] = new TemplateEvaluationException(e);
				}
			}
		}
	}
}
