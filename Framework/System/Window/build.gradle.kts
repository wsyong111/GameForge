import io.github.wsyong11.gameforge.dependencies.*

plugins{
	kotlin("jvm")
}

dependencies {
	implementation(project, JOML)
	implementation(project, LWJGL)
	implementation(project, LWJGL_GLFW)
	implementation(project, LWJGL_OPENGL)

	implementation(project(":Framework:Listener"))
	implementation(project(":Util"))
	implementation(project(":Framework:Annotation"))
	implementation(project(":Framework:Common"))
}
