import io.github.wsyong11.gameforge.project.includeDebug
import io.github.wsyong11.gameforge.project.includeRelease

//language=jvm-class-name
val mainClassName: String = "io.github.wsyong11.gameforge.Main"

plugins {
	java
	application
	id("com.gradleup.shadow")
}

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
	exclude("META-INF/*LICENSE*")
	exclude("META-INF/*NOTICE*")
	exclude("META-INF/*README*")

	if (!includeDebug) exclude(
		"META-INF/maven/**",
		"META-INF/gradle/**",
		"META-INF/proguard/**"
	)
}


tasks.register("cleanAll") {
	dependsOn(subprojects
		.map { it.tasks }
		.mapNotNull { it.findByName("clean") })
}


afterEvaluate {
	println("Run configuration:")
	println("| Include Debug: ${includeDebug}")
	println("| Include Release: ${includeRelease}")
}