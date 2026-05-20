import io.github.wsyong11.gameforge.dependencies.SEMVER4J;
import io.github.wsyong11.gameforge.dependencies.implementation

plugins {
	kotlin("jvm")
}

dependencies {
	implementation(project, SEMVER4J)

	implementation(project(":Framework:Common"))
}
