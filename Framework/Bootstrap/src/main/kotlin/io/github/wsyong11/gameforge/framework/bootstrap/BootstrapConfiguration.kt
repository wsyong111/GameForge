package io.github.wsyong11.gameforge.framework.bootstrap

import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel
import org.jetbrains.annotations.Nullable
import java.nio.file.Path

@JvmRecord
data class BootstrapConfiguration<T>(
	@get:JvmName("getLogDir")
	val logDir: Path,

	@get:JvmName("isDebug")
	val debug: Boolean,

	@get:JvmName("getLogLevel")
	val logLevel: LogLevel,

	@get:JvmName("getConfig")
	@get:Nullable
	val config: T?,
)
