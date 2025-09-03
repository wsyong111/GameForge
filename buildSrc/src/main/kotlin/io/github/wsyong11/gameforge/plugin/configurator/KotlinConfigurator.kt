package io.github.wsyong11.gameforge.plugin.configurator

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

internal class KotlinConfigurator : Configurator {
	override fun apply(project: Project) {
		project.tasks.withType<KotlinCompile>().configureEach {
			kotlinOptions {
				moduleName = project.path.removePrefix(":").replace(":", "_")
			}
		}
	}
}