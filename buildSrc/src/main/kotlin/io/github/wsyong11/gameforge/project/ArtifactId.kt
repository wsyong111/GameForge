package io.github.wsyong11.gameforge.project

import org.gradle.api.Project
import org.gradle.kotlin.dsl.extra
import org.jetbrains.kotlin.gradle.plugin.extraProperties

const val ARTIFACT_ID_NAME = "artifactId"
const val EMPTY_ARTIFACT_ID = ""

var Project.artifactId: String
    get() {
	    return this.extraProperties.properties[ARTIFACT_ID_NAME] as? String ?: EMPTY_ARTIFACT_ID
    }
    set(value) {
        this.extra[ARTIFACT_ID_NAME] = value
    }
