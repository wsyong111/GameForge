import io.github.wsyong11.gameforge.dependencies.*

dependencies {
	implementation(JACKSON)
	implementation(JACKSON_TOML)
	implementation(JACKSON_YAML)
	implementation(JSON_PATH)

	implementation(project(":Framework:Common"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:System:Log"))

	implementation(project(":Util"))
	implementation(project(":Framework:DataFlow"))
	implementation(project(":Framework:RichText"))
}