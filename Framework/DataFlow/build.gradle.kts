import io.github.wsyong11.gameforge.dependencies.JACKSON
import io.github.wsyong11.gameforge.dependencies.JACKSON_TOML
import io.github.wsyong11.gameforge.dependencies.JACKSON_YAML
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.project.artifactId

plugins {
//	kotlin("jvm")
}

artifactId = "DataFlow"

dependencies {
	implementation(project, JACKSON)
	implementation(project, JACKSON_TOML)
	implementation(project, JACKSON_YAML)

	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:Annotation"))
	implementation(project(":Util"))
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:Lang"))
}