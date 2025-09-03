import io.github.wsyong11.gameforge.project.artifactId

plugins {
	kotlin("jvm")
}

artifactId = "CommonCore"

dependencies {
	implementation(project(":Game:Common"))
	implementation(project(":Util"))

	implementation(project(":Framework:Application"))
	implementation(project(":Framework:Lifecycle"))
	implementation(project(":Framework:Event"))
	implementation(project(":Framework:System:Resource"))
}
