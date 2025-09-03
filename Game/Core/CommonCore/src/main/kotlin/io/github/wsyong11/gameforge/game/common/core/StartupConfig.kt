package io.github.wsyong11.gameforge.game.common.core

import io.github.wsyong11.gameforge.game.common.GameEnvConfig
import java.nio.file.Path
import java.util.*

data class StartupConfig(
	private val logDir: Path,
	private val tempDir: Path,
	private val modDir: Path,
	private val configDir: Path,
	private val crashReportDir: Path,
	private val debug: Boolean,
	private val modJarPaths: List<Path>,
	private val safeMode: Boolean,
) : GameEnvConfig {
	override fun getLogDir() = this.logDir

	override fun getTempDir() = this.tempDir

	override fun getModDir() = this.modDir

	override fun getConfigDir() = this.configDir

	override fun getCrashReportDir() = this.crashReportDir

	override fun isDebug() = this.debug

	override fun getModJarPaths(): List<Path> =
		Collections.unmodifiableList(this.modJarPaths)

	override fun isSafeMode() = this.safeMode
}
