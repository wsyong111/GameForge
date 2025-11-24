package io.github.wsyong11.gameforge.plugin.project.configurator

import io.github.wsyong11.gameforge.plugin.project.ProjectConfigExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

internal class JavaConfigurator : Configurator {
    override fun apply(project: Project) {
    }

    override fun afterApply(project: Project) {
        val javaExt = project.extensions.findByType(JavaPluginExtension::class.java)
        val projectExt = project.extensions.findByType(ProjectConfigExtension::class.java)
        javaExt?.apply {
            val sourceVersion = projectExt?.javaSourceVersion
            val compileVersion = projectExt?.javaCompileVersion

            if (sourceVersion != null) sourceCompatibility = sourceVersion
            if (compileVersion != null) targetCompatibility = compileVersion
        }
    }
}