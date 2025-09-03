package io.github.wsyong11.gameforge.plugin.configurator

import org.gradle.api.Project

internal fun interface Configurator {
	fun apply(project: Project)
}