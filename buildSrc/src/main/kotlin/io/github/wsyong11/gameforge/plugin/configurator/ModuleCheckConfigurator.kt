package io.github.wsyong11.gameforge.plugin.configurator

import org.gradle.api.Project
import org.gradle.api.Project.DEFAULT_VERSION
import io.github.wsyong11.gameforge.project.*

internal class ModuleCheckConfigurator : Configurator {
	override fun apply(project: Project) {
		project.afterEvaluate {
			if (version == DEFAULT_VERSION)
				logger.warn("Project ${project.path} is using the default version '${DEFAULT_VERSION}'. Did you forget to set project.version?")

			if (description == null)
				logger.warn("Project ${project.path} is not have description.")

			if (artifactId == EMPTY_ARTIFACT_ID)
				logger.warn("Project ${project.path} is not have artifactId.")
		}
	}
}