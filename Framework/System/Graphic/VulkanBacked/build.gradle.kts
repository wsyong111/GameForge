import io.github.wsyong11.gameforge.dependencies.LWJGL
import io.github.wsyong11.gameforge.dependencies.LWJGL_VULKAN
import io.github.wsyong11.gameforge.dependencies.SEMVER4J
import io.github.wsyong11.gameforge.dependencies.implementation

dependencies {
	implementation(project, LWJGL)
	implementation(project, LWJGL_VULKAN)
	implementation(project, SEMVER4J)

	implementation(project(":Util"))
}
