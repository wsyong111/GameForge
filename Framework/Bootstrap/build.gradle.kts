import io.github.wsyong11.gameforge.dependencies.JLINE
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.project.artifactId

plugins {
	kotlin("jvm")
}

artifactId = "Bootstrap"

dependencies {
	implementation(project(":Framework:Application"))
	implementation(project(":Framework:Lifecycle"))
	implementation(project(":Framework:System:Log"))

	implementation(project(":Util"))
}
