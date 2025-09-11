import io.github.wsyong11.gameforge.dependencies.*
import io.github.wsyong11.gameforge.project.artifactId

artifactId = "ResourceSystem"

dependencies {
	implementation(project, JACKSON)
	implementation(project, JACKSON_TOML)
	implementation(project, JACKSON_YAML)
	implementation(project, JSON_PATH)

	implementation(project(":Framework:Common"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:System:Log"))

	implementation(project(":Util"))
	implementation(project(":Framework:DataFlow"))
	implementation(project(":Framework:RichText"))
}