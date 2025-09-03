package io.github.wsyong11.gameforge.dependencies

import org.gradle.api.artifacts.dsl.DependencyHandler

fun DependencyHandler.`implementation`(id: Dependency) =
	add("implementation", dependence(id))

fun DependencyHandler.`compileOnly`(id: Dependency) =
	add("compileOnly", dependence(id))

fun DependencyHandler.`runtimeOnly`(id: Dependency) =
	add("runtimeOnly", dependence(id))

fun DependencyHandler.`annotationProcessor`(id: Dependency) =
	add("annotationProcessor", dependence(id))


fun DependencyHandler.`testImplementation`(id: Dependency) =
	add("testImplementation", dependence(id))

fun DependencyHandler.`testCompileOnly`(id: Dependency) =
	add("testCompileOnly", dependence(id))

fun DependencyHandler.`testAnnotationProcessor`(id: Dependency) =
	add("testAnnotationProcessor", dependence(id))