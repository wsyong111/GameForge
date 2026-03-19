import io.github.wsyong11.gameforge.dependencies.*

dependencies {
	implementation(project, JOML)
	implementation(project, LWJGL_OPENAL)
	implementation(project, LWJGL_STB)
	implementation(project, FAST_UTIL)

	implementation(project(":Util"))
	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:Mime"))
	implementation(project(":Framework:System:Resource"))
}

junit()

