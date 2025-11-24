package io.github.wsyong11.gameforge.plugin.project

import org.gradle.api.JavaVersion

open class ProjectConfigExtension {
    var javaSourceVersion: JavaVersion? = null
    var javaCompileVersion: JavaVersion? = null
}