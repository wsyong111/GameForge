import io.github.wsyong11.gameforge.dependencies.JOML
import io.github.wsyong11.gameforge.dependencies.implementation

dependencies {
	implementation(project, JOML)

	implementation(project(":Framework:Common"))
	implementation(project(":Util"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:Tick"))
}
