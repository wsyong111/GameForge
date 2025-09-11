import io.github.wsyong11.gameforge.dependencies.JOML
import io.github.wsyong11.gameforge.dependencies.LWJGL
import io.github.wsyong11.gameforge.dependencies.LWJGL_OPENGL
import io.github.wsyong11.gameforge.dependencies.implementation

plugins{
	kotlin("jvm")
}

dependencies {
	implementation(project, JOML)
	implementation(project, LWJGL)
	implementation(project, LWJGL_OPENGL)

	implementation(project(":Framework:Listener"))
	implementation(project(":Util"))
	implementation(project(":Framework:Annotation"))
}
