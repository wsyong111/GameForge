//language=jvm-class-name
val mainClassName: String = "io.github.wsyong11.gameforge.Main"

plugins {
	id("java")
	id("com.gradleup.shadow")
}

group = "io.github.wsyong11.gameforge"
version = "0.0.0-beta"

allprojects {
	group = "io.github.wsyong11.gameforge"
	version = "0.0.0-beta"

	apply(plugin = "io.github.wsyong11.gameforge.plugin")

	repositories {
		mavenCentral()
	}
}

dependencies {
	implementation(project(":Game:Loader"))

	implementation(project(":Framework:Bootstrap"))
	implementation(project(":Framework:System:Log"))
}

tasks.jar {
	manifest {
		attributes(
			"Manifest-Version" to "1.0",
			"Main-Class" to mainClassName,
			"Implementation-Version" to version
		)
	}
}

tasks.shadowJar {
    mergeServiceFiles()

    exclude("META-INF/DEPENDENCIES")
    exclude("META-INF/LICENSE*")
    exclude("META-INF/NOTICE*")
    exclude("META-INF/README*")
}

tasks.register("cleanAll") {
	dependsOn(subprojects.map { it.tasks }.mapNotNull { it.findByName("clean") })
}
