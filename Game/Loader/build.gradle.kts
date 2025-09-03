import io.github.wsyong11.gameforge.dependencies.COMMONS_CLI
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.project.artifactId

artifactId = "Loader"

dependencies {
    implementation(COMMONS_CLI)

    implementation(project(":Game:Core:CommonCore"))
    implementation(project(":Game:Core:ClientCore"))
    implementation(project(":Game:Core:ServerCore"))

    implementation(project(":Framework:Application"))
    implementation(project(":Framework:System:Log"))
    implementation(project(":Framework:Bootstrap"))
}
