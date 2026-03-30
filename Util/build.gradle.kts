import io.github.wsyong11.gameforge.dependencies.FAST_UTIL
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.dependencies.junit
import io.github.wsyong11.gameforge.project.artifactId

plugins {
	kotlin("jvm")
	id("me.champeau.jmh")
}

group = "io.github.wsyong11.gameforge.util"
artifactId = "Util"

dependencies {
	implementation(project, FAST_UTIL)
}

junit()
