package io.github.wsyong11.gameforge.util

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionAware


internal fun String.appendIfNotNull(value: String?, prefix: String = "", stffux: String = "") =
	if (value == null) this else this + prefix + value + stffux
