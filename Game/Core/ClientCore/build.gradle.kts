import io.github.wsyong11.gameforge.project.artifactId

artifactId = "ClientCore"

dependencies {
	implementation(project(":Game:Core:CommonCore"))
	implementation(project(":Game:Client"))
	implementation(project(":Game:Common"))

	implementation(project(":Framework:Application"))
	implementation(project(":Framework:Lifecycle"))
	implementation(project(":Framework:System:Resource"))
}
