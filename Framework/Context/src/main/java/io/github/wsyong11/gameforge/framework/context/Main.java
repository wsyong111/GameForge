package io.github.wsyong11.gameforge.framework.context;

import io.github.wsyong11.gameforge.framework.env.EnvConfig;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;

public class Main {
	public static void main(String[] args) {
		ServiceContext context = ServiceContext
			.builder()
			.item(EnvConfig.class, EnvConfig
				.builder()
				.item(EnvConfig.DEBUG, true)
				.item(EnvConfig.LOG_LEVEL, LogLevel.DEBUG)
				.build())
			.build();
	}
}
