package io.github.wsyong11.gameforge.framework.app

import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel
import java.nio.file.Path
import java.util.function.Supplier

data class BootstrapContext(
	val logDir: Path,
	val logLevel: LogLevel,
	@get:JvmName("isDebug")
	val debug: Boolean,
	private val configs: Map<BootstrapConfig<*>, Any>,
) {
	@Suppress("UNCHECKED_CAST")
	fun <T> getConfig(config: BootstrapConfig<T>) =
		this.configs[config] as T?

	fun <T> getConfigRequire(config: BootstrapConfig<T>) =
		this.getConfig(config) ?: throw IllegalArgumentException("Require config field ${config.name}: ${config.type}")

	fun <T> getConfig(config: BootstrapConfig<T>, default: T) =
		this.getConfig(config) ?: default

	fun <T> getConfig(config: BootstrapConfig<T>, default: Supplier<T>) =
		this.getConfig(config) ?: default.get()
}