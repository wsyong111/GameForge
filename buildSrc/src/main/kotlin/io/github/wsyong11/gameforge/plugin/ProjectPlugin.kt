package io.github.wsyong11.gameforge.plugin

import io.github.wsyong11.gameforge.plugin.configurator.Configurator
import io.github.wsyong11.gameforge.plugin.configurator.DependenciesConfigurator
import io.github.wsyong11.gameforge.plugin.configurator.KotlinConfigurator
import io.github.wsyong11.gameforge.plugin.configurator.ModuleCheckConfigurator
import io.github.wsyong11.gameforge.project.visual
import io.github.wsyong11.gameforge.project.ignoreDefaultConfig
import org.gradle.api.Plugin
import org.gradle.api.Project


private val configurator: List<() -> Configurator> = listOf(
	::DependenciesConfigurator,
	::ModuleCheckConfigurator,
	::KotlinConfigurator,
)

class ProjectPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		if (project.visual) {
			project.logger.debug("Skip visual project ${project.path}")
			return
		}

		if (project.ignoreDefaultConfig)
			return

		project.pluginManager.apply("java")

		configurator
			.map { it() }
			.forEach { it.apply(project) }
	}
}