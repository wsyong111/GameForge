import io.github.wsyong11.gameforge.project.artifactId

plugins {
	`java-library`
}

artifactId = "EnvConfig"

dependencies {
	api(project(":Framework:Context"))
	implementation(project(":Util"))
}
