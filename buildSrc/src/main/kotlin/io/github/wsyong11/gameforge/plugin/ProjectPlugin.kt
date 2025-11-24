package io.github.wsyong11.gameforge.plugin

import io.github.wsyong11.gameforge.plugin.project.ProjectConfigExtension
import io.github.wsyong11.gameforge.plugin.project.configurator.*
import io.github.wsyong11.gameforge.project.ignoreDefaultConfig
import io.github.wsyong11.gameforge.project.visual
import org.gradle.api.Plugin
import org.gradle.api.Project

private val configurator: List<() -> Configurator> = listOf(
    ::DependenciesConfigurator,
    ::ModuleCheckConfigurator,
    ::JavaConfigurator,
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

        project.extensions.create("projectConfig", ProjectConfigExtension::class.java)

        configurator
            .map { it() }
            .forEach {
                it.apply(project)
                project.afterEvaluate(it::afterApply)
            }
    }
}