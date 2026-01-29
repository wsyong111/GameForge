import io.github.wsyong11.gameforge.dependencies.LWJGL_OPENAL
import io.github.wsyong11.gameforge.dependencies.implementation

dependencies {
	implementation(project, LWJGL_OPENAL)

	implementation(project(":Util"))
	implementation(project(":Framework:System:Log"))
}
