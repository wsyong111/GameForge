package io.github.wsyong11.gameforge.project

import org.gradle.api.Project

var Project.ignoreDefaultConfig: Boolean
	get() {
		return this["_disableDefaultConfig"] as? Boolean ?: false
	}
	set(value) {
		this["_disableDefaultConfig"] = value
	}
