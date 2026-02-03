import io.github.wsyong11.gameforge.dependencies.JOML
import io.github.wsyong11.gameforge.dependencies.LWJGL_OPENAL
import io.github.wsyong11.gameforge.dependencies.LWJGL_STB
import io.github.wsyong11.gameforge.dependencies.implementation

dependencies {
	implementation(project, JOML)
	implementation(project, LWJGL_OPENAL)
	implementation(project, LWJGL_STB)

	implementation(project(":Util"))
	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:Common"))
}
