package io.github.wsyong11.gameforge.plugin.configurator

import io.github.wsyong11.gameforge.dependencies.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.kotlin
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal class DependenciesConfigurator : Configurator {
	override fun apply(project: Project) {
		project.repositories.mavenCentral()
		project.repositories.mavenLocal()

		project.dependencies.compileOnly(project, LOMBOK)

		project.dependencies.implementation(project, ANNOTATIONS)

		project.dependencies.implementation(project, GUAVA)
		project.dependencies.implementation(project, COMMONS_LANG)
		project.dependencies.implementation(project, COMMONS_COLLECTIONS)

		project.dependencies.implementation(project, AUTO_SERVICE)
		project.dependencies.annotationProcessor(project, AUTO_SERVICE)

		val kotlinExtension = project.extensions.findByType<KotlinJvmProjectExtension>()
		if (kotlinExtension != null)
			project.dependencies.add("implementation", project.dependencies.kotlin("stdlib"))
	}
}