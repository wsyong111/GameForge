import io.github.wsyong11.gameforge.dependencies.*
import io.github.wsyong11.gameforge.dependencies.implementation

dependencies {
    implementation(project, JAVA_POET)
//    implementation(project, AUTO_COMMON)

    implementation(project(":Framework:Config"))
    implementation(project(":Framework:DataFlow"))
}
