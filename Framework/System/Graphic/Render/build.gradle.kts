import io.github.wsyong11.gameforge.dependencies.FAST_UTIL
import io.github.wsyong11.gameforge.dependencies.JOML
import io.github.wsyong11.gameforge.dependencies.implementation

dependencies {
	implementation(project, FAST_UTIL)
	implementation(project, JOML)

	implementation(project(":Util"))

	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:System:Log"))
//	implementation(project(":Framework:System:Resource"))
//	implementation(project(":Framework:System:Window"))
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:EnvConfig"))
	implementation(project(":Framework:Context"))
	implementation(project(":Framework:Annotation"))
}
