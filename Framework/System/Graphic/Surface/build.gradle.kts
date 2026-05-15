import io.github.wsyong11.gameforge.dependencies.*

plugins{
	kotlin("jvm")
}

dependencies {
	implementation(project, JOML)
	implementation(project, LWJGL)
	implementation(project, LWJGL_GLFW)
	implementation(project, LWJGL_OPENGL)
	implementation(project, LWJGL_STB)

	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:Annotation"))
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:System:Log"))
	implementation(project(":Util"))
	implementation(project(":Framework:System:Graphic:GraphicCore"))
//	implementation(project(":Framework:System:Graphic:Core"))
}
