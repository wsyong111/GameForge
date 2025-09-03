import io.github.wsyong11.gameforge.dependencies.JOML
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.project.artifactId

artifactId = "Common"

dependencies {
	implementation(JOML)

	implementation(project(":Util"))
}
