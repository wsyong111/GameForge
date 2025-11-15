import io.github.wsyong11.gameforge.dependencies.JOML
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.project.artifactId

artifactId = "ClientCore"

dependencies {
	implementation(project, JOML)

	implementation(project(":Game:Core:CommonCore"))
	implementation(project(":Game:Client"))
	implementation(project(":Game:Common"))

	implementation(project(":Framework:Lifecycle"))
	implementation(project(":Framework:Event"))
	implementation(project(":Framework:System:Resource"))
	implementation(project(":Framework:System:Render"))
	implementation(project(":Framework:System:Window"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Util"))
	implementation(project(":Framework:System:Input"))
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:App"))
	implementation(project(":Framework:I18n"))
}
