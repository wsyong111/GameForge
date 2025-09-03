package io.github.wsyong11.gameforge.project

import org.gradle.api.Project

val Project.visual: Boolean
	get() {
		return !this.buildFile.exists()
	}