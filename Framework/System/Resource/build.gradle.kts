import io.github.wsyong11.gameforge.dependencies.RX_JAVA
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.project.artifactId

artifactId = "ResourceSystem"

dependencies {
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:RichText"))

	implementation(project(":Util"))
	implementation(project(":Framework:Mime"))
	implementation(project(":Framework:SPI"))

	implementation(project, RX_JAVA)
}