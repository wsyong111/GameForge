package io.github.wsyong11.gameforge.dependencies

import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.exclude
import org.gradle.api.artifacts.Dependency as MavenDependency

private fun DependencyHandler.addDependency(configuration: String, dependency: Dependency): MavenDependency? {
	return add(configuration, dependency.toString()) {
		dependency.exclude.forEach {
			exclude(it.group, it.artifact)
		}
	}
}

fun DependencyHandler.`implementation`(id: Dependency)=
	addDependency("implementation", id)

fun DependencyHandler.`compileOnly`(id: Dependency) =
	addDependency("compileOnly", id)

fun DependencyHandler.`runtimeOnly`(id: Dependency) =
	addDependency("runtimeOnly", id)

fun DependencyHandler.`annotationProcessor`(id: Dependency) =
	addDependency("annotationProcessor", id)


fun DependencyHandler.`testImplementation`(id: Dependency) =
	addDependency("testImplementation", id)

fun DependencyHandler.`testCompileOnly`(id: Dependency) =
	addDependency("testCompileOnly", id)

fun DependencyHandler.`testAnnotationProcessor`(id: Dependency) =
	addDependency("testAnnotationProcessor", id)