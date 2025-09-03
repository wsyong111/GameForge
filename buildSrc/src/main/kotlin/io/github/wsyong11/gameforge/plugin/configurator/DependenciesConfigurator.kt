package io.github.wsyong11.gameforge.plugin.configurator

import io.github.wsyong11.gameforge.dependencies.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal class DependenciesConfigurator : Configurator {
	override fun apply(project: Project) {
		project.dependencies.compileOnly(LOMBOK)

		project.dependencies.implementation(ANNOTATIONS)

		project.dependencies.implementation(GUAVA)
		project.dependencies.implementation(COMMONS_LANG)
		project.dependencies.implementation(COMMONS_COLLECTIONS)

		project.dependencies.implementation(AUTO_SERVICE)
		project.dependencies.annotationProcessor(AUTO_SERVICE)

		val kotlinExtension = project.extensions.findByType<KotlinJvmProjectExtension>()
		if (kotlinExtension != null)
			project.dependencies.add("implementation", project.dependencies.kotlin("stdlib"))
	}
}