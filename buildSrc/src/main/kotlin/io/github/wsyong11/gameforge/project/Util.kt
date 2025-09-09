package io.github.wsyong11.gameforge.project

import org.gradle.api.Project

val Project.visual: Boolean
	get() {
		return !this.buildFile.exists()
	}

fun Project.getOutputDir(name: String) =
	this.layout.buildDirectory.dir("generated/${name}").get()

fun Project.getOutputFile(name: String) =
	this.layout.buildDirectory.file("generated/${name}").get()

operator fun Project.get(property: String): Any? =
	if (this.hasProperty(property)) this.property(property) else null

operator fun Project.set(property: String, value: Any?) =
	this.setProperty(property, value)

