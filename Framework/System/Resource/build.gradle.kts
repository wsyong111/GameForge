import io.github.wsyong11.gameforge.dependencies.JACKSON
import io.github.wsyong11.gameforge.dependencies.JACKSON_TOML
import io.github.wsyong11.gameforge.dependencies.JACKSON_YAML
import io.github.wsyong11.gameforge.dependencies.implementation

dependencies{
	implementation(JACKSON)
	implementation(JACKSON_TOML)
	implementation(JACKSON_YAML)

	implementation(project(":Framework:Common"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:System:Log"))

	implementation(project(":Util"))
	implementation(project(":Framework:DataFlow"))
	implementation(project(":Framework:RichText"))
}