package io.github.wsyong11.gameforge.dependencies

import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

enum class ImportType(
	val typeName: String,
) {
	IMPLEMENTATION("implementation"),
	COMPILE_ONLY("compileOnly"),
	RUNTIME_ONLY("runtimeOnly"),
	ANNOTATION_PROCESSOR("annotationProcessor");

	val testTypeName
		get() = "test" + this.typeName.uppercaseFirstChar()
}

fun DependencyHandler.addDependency(
	project: Project,
	type: ImportType,
	dependency: Dependency,
	testDependency: Boolean = false,
) {
	val configuration = if (testDependency)
		type.testTypeName else type.typeName

	val added = project.configurations.getByName(configuration).allDependencies.any {
		it.group == dependency.group &&
			it.name == dependency.artifact &&
			(it.version == null || it.version == dependency.version)
	}
	if (added)
		return

	val dependencyName: Any = when (dependency.type) {
		DependencyType.BOM -> platform(dependency.toString())
		DependencyType.DEPENDENCY -> dependency.toString()
	}

	val mavenDenepdencyItem = this.add(configuration, dependencyName) as? ModuleDependency

	dependency.exclude.forEach {
		mavenDenepdencyItem?.exclude(it.group, it.artifact)
	}

	dependency.additionDependency.forEach {
		addDependency(project, it.first, it.second, testDependency)
	}
}

fun DependencyHandler.implementation(project: Project, id: Dependency) =
	addDependency(project, ImportType.IMPLEMENTATION, id)

fun DependencyHandler.compileOnly(project: Project, id: Dependency) =
	addDependency(project, ImportType.COMPILE_ONLY, id)

fun DependencyHandler.runtimeOnly(project: Project, id: Dependency) =
	addDependency(project, ImportType.RUNTIME_ONLY, id)

fun DependencyHandler.annotationProcessor(project: Project, id: Dependency) =
	addDependency(project, ImportType.ANNOTATION_PROCESSOR, id)


fun DependencyHandler.testImplementation(project: Project, id: Dependency) =
	addDependency(project, ImportType.IMPLEMENTATION, id, true)

fun DependencyHandler.testCompileOnly(project: Project, id: Dependency) =
	addDependency(project, ImportType.COMPILE_ONLY, id, true)

fun DependencyHandler.testAnnotationProcessor(project: Project, id: Dependency) =
	addDependency(project, ImportType.ANNOTATION_PROCESSOR, id, true)