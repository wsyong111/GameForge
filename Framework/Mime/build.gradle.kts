import io.github.wsyong11.gameforge.dependencies.GSON
import io.github.wsyong11.gameforge.dependencies.implementation

dependencies {
	implementation(project, GSON)

	implementation(project(":Framework:Common"))
	implementation(project(":Framework:System:Log"))
}
