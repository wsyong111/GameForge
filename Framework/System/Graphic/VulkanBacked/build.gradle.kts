import io.github.wsyong11.gameforge.dependencies.LWJGL
import io.github.wsyong11.gameforge.dependencies.LWJGL_VULKAN
import io.github.wsyong11.gameforge.dependencies.SEMVER4J
import io.github.wsyong11.gameforge.dependencies.implementation

plugins{
	kotlin("jvm")
}

dependencies {
	implementation(project, LWJGL)
	implementation(project, LWJGL_VULKAN)
	implementation(project, SEMVER4J)

	implementation(project(":Util"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:Common"))
}
