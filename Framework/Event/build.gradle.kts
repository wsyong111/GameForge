import io.github.wsyong11.gameforge.dependencies.GEANTYREF
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.dependencies.junit
import io.github.wsyong11.gameforge.project.artifactId

artifactId = "Event"

dependencies {
	implementation(GEANTYREF)

	implementation(project(":Util"))
	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:EnvConfig"))
}

junit()
