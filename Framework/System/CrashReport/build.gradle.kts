import io.github.wsyong11.gameforge.dependencies.OSHI
import io.github.wsyong11.gameforge.dependencies.implementation

plugins {
	kotlin("jvm")
}

dependencies {
	implementation(project, OSHI)

	implementation(project(":Framework:Annotation"))
	implementation(project(":Util"))
	implementation(project(":Framework:System:Log"))
}
