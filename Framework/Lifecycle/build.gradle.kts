import io.github.wsyong11.gameforge.project.artifactId

artifactId = "Lifecycle"

dependencies {
	implementation(project(":Framework:EnvConfig"))
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:System:Log"))

	implementation(project(":Util"))
}
