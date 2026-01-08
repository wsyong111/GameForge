import io.github.wsyong11.gameforge.dependencies.FAST_UTIL
import io.github.wsyong11.gameforge.dependencies.JOML
import io.github.wsyong11.gameforge.dependencies.annotationProcessor
import io.github.wsyong11.gameforge.dependencies.implementation
import io.github.wsyong11.gameforge.project.artifactId

artifactId = "RenderSystem"

dependencies {
	implementation(project, FAST_UTIL)
	implementation(project, JOML)
	implementation(project(":Framework:System:Log"))
	implementation(project(":Framework:System:Resource"))
	implementation(project(":Framework:Listener"))
	implementation(project(":Framework:DataFlow"))
	implementation(project(":Util"))
	implementation(project(":Framework:System:Window"))
	implementation(project(":Framework:Common"))
	implementation(project(":Framework:Annotation"))
	implementation(project(":Framework:Config"))
	annotationProcessor(project(":Framework:Config:AnnotationProcessor"))
}
