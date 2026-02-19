import io.github.wsyong11.gameforge.project.artifactId
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.dependencies.FAST_UTIL

plugins {
	kotlin("jvm")
}

group = "io.github.wsyong11.gameforge.util"
artifactId = "Util"

dependencies {
	implementation(project, FAST_UTIL)
}
