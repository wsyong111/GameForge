import io.github.wsyong11.gameforge.dependencies.GSON
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.project.artifactId

plugins {
	kotlin("jvm")
}

artifactId = "DataFlow"

dependencies {
	implementation(project, GSON)

	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:Annotation"))
	implementation(project(":Util"))
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:Lang"))
}