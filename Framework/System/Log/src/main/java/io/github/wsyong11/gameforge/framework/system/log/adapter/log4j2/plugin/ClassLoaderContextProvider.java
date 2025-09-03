package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2.plugin;

import io.github.wsyong11.gameforge.util.reflect.ClassLoaderUtils;
import com.google.auto.service.AutoService;
import org.apache.logging.log4j.core.util.ContextDataProvider;

import java.util.Map;

@AutoService(ContextDataProvider.class)
public class ClassLoaderContextProvider implements ContextDataProvider {
	@Override
	public Map<String, String> supplyContextData() {
		return Map.of("classLoader", ClassLoaderUtils.getName(Thread.currentThread().getContextClassLoader()));
	}
}
