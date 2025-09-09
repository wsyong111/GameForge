package io.github.wsyong11.gameforge.project

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val Project.buildType: BuildType
	get() {
		if (!this.hasProperty("buildType"))
			return BuildType.DEBUG

		val type = this.property("buildType") as? String
		if (type == null)
			return BuildType.DEBUG

		return BuildType.valueOf(type)
	}

val Project.includeDebug
	get() = this.buildType == BuildType.DEBUG || this.hasProperty("includeDebug")

val Project.includeRelease
	get() = true
//	get() = this.buildType == BuildType.RELEASE || this.hasProperty("includeRelease")

enum class BuildType {
	RELEASE,
	DEBUG
}


fun Project.onlyRelease() {
	tasks.withType<JavaCompile> {
		onlyIf { includeRelease }
	}
	tasks.withType<KotlinCompile> {
		onlyIf { includeRelease }
	}
}

fun Project.onlyDebug() {
	tasks.withType<JavaCompile> {
		onlyIf { includeDebug }
	}
	tasks.withType<KotlinCompile> {
		onlyIf { includeDebug }
	}
}
