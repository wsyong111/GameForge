package io.github.wsyong11.gameforge.dependencies

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.named

fun Project.junit() {
	this.dependencies.add("testImplementation", this.dependencies.platform("org.junit:junit-bom:5.10.0"))
	this.dependencies.add("testImplementation", "org.junit.jupiter:junit-jupiter")

	this.tasks.named<Test>("test") {
		useJUnitPlatform()
	}
}