package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2.plugin;

import com.google.auto.service.AutoService;
import io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2.Log4jAdapter;
import org.apache.logging.log4j.core.util.ContextDataProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@AutoService(ContextDataProvider.class)
public class LogDirContextProvider implements ContextDataProvider {
	@Nullable
	private static Log4jAdapter adapter;

	public static void setAdapter(@Nullable Log4jAdapter adapter) {
		LogDirContextProvider.adapter = adapter;
	}

	@Override
	public Map<String, String> supplyContextData() {
		if (adapter == null)
			return Map.of("logDir", "log");
		return Map.of("logDir", adapter.getLogDir());
	}
}
