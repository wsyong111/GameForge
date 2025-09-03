import io.github.wsyong11.gameforge.dependencies.*

dependencies {
	implementation(JACKSON)
	implementation(JACKSON_TOML)
	implementation(JACKSON_YAML)

	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:Annotation"))
	implementation(project(":Util"))
	implementation(project(":Framework:Common"))
}