package io.github.wsyong11.gameforge.plugin.project.configurator

import org.gradle.api.Project

internal fun interface Configurator {
    fun apply(project: Project)
    fun afterApply(project: Project) {}
}