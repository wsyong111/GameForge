import io.github.wsyong11.gameforge.dependencies.JANSI
import io.github.wsyong11.gameforge.dependencies.JLINE
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.dependencies.runtimeOnly
import io.github.wsyong11.gameforge.project.artifactId

artifactId = "ServerCore"

dependencies {
	implementation(JLINE)
	runtimeOnly(JANSI)

	implementation(project(":Framework:Application"))
	implementation(project(":Framework:Lifecycle"))
	implementation(project(":Framework:System:Log"))
	implementation(project(":Game:Core:CommonCore"))
	implementation(project(":Game:Common"))

	implementation(project(":Framework:Listener"))
	implementation(project(":Util"))
}
