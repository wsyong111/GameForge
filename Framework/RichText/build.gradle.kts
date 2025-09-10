import io.github.wsyong11.gameforge.project.artifactId

plugins {
	kotlin("jvm")
}

artifactId = "RichText"

dependencies {
	implementation(project(":Util"))
	implementation(project(":Framework:Common"))
}


