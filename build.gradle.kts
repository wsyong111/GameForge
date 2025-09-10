import io.github.wsyong11.gameforge.project.ignoreDefaultConfig
import io.github.wsyong11.gameforge.project.includeDebug
import io.github.wsyong11.gameforge.project.includeRelease
import io.github.wsyong11.gameforge.project.visual

//language=jvm-class-name
val mainClassName: String = "io.github.wsyong11.gameforge.Main"

plugins {
	java
	id("com.gradleup.shadow")
}

allprojects {
	group = "io.github.wsyong11.gameforge"
	version = "0.0.0-beta"

	apply(plugin = "io.github.wsyong11.gameforge.plugin")
}

subprojects {
	if (project.visual || project.ignoreDefaultConfig)
		return@subprojects

	if (project.path == ":Assets")
		return@subprojects

	dependencies {
		implementation(project(":Assets"))
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


tasks.register<Javadoc>("aggregateJavadoc") {
	val sourceSets = subprojects
		.mapNotNull { it.extensions.findByType<JavaPluginExtension>() }
		.map { it.sourceSets }
		.map { it.getByName("main") }

	setSource(sourceSets.map { it.allJava })
	classpath = files(sourceSets.map { it.compileClasspath })

	(options as StandardJavadocDocletOptions).apply {
		encoding = "UTF-8"
		windowTitle = "Game Forge API"
		docTitle = "Game Forge API Documentation"
		version = true
		tags(
            "apiNote:a:API Note:",
            "implSpec:m:Implementation Spec:",
            "implNote:c:Implementation Note:"
        )
	}
}


afterEvaluate {
	println("Run configuration:")
	println("| Include Debug: ${includeDebug}")
	println("| Include Release: ${includeRelease}")
}