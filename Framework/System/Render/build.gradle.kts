import io.github.wsyong11.gameforge.dependencies.JOML
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.project.artifactId

artifactId = "RenderSystem"

dependencies {
	implementation(project, JOML)
	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:System:Resource"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Util"))
}
