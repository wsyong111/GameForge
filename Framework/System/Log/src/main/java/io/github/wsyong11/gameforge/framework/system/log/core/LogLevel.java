package io.github.wsyong11.gameforge.framework.system.log.core;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public enum LogLevel {
	TRACE(0),
	DEBUG(1),
	INFO(2),
	WARN(3),
	ERROR(4);

	private static final Map<String, LogLevel> STRING_LOG_LEVEL_MAP = Map.of(
		"trace", LogLevel.TRACE,
		"t", LogLevel.TRACE,
		"debug", LogLevel.DEBUG,
		"d", LogLevel.DEBUG,
		"info", LogLevel.INFO,
		"i", LogLevel.INFO,
		"warn", LogLevel.WARN,
		"w", LogLevel.WARN,
		"error", LogLevel.ERROR,
		"e", LogLevel.ERROR
	);

	/**
	 * 通过字符串获取日志等级枚举, 兼容单个字符 {@code t, d, i, w, e} 和字符串 {@code trace, debug, info, warn, error}，
	 * 不区分大小写，此函数是空指针安全的
	 *
	 * @param type 日志等级字符串, 如果为 {@code null} 则返回 {@code null}
	 * @return 日志等级枚举
	 */
	@Contract("null -> null")
	@Nullable
	public static LogLevel parse(@Nullable String type) {
		if (type == null)
			return null;

		return STRING_LOG_LEVEL_MAP.get(type.toLowerCase(Locale.ROOT));
	}

	private final int level;

	LogLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

	@Override
	public String toString() {
		return this.name() + "[" + this.level + "]";
	}
}
