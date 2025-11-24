package io.github.wsyong11.gameforge.plugin.project.configurator

import io.github.wsyong11.gameforge.plugin.project.ProjectConfigExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal class KotlinConfigurator : Configurator {
    override fun apply(project: Project) {
    }

    override fun afterApply(project: Project) {
        val projectExt = project.extensions.findByType(ProjectConfigExtension::class.java)

        project.tasks.withType<KotlinCompile>().configureEach {
            kotlinOptions {
                moduleName = project.path.removePrefix(":").replace(":", "_")

                val jvmVersion = projectExt?.javaCompileVersion
                if (jvmVersion != null)
                    jvmTarget = jvmVersion.toString()
            }
        }
    }
}