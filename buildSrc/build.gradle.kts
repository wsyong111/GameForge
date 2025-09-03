plugins {
	`kotlin-dsl`
	id("java-gradle-plugin")
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
	implementation("com.palantir.javapoet:javapoet:0.5.0")
}

gradlePlugin {
	plugins {
		create("projectPlugin") {
			id = "io.github.wsyong11.gameforge.plugin"
			implementationClass = "io.github.wsyong11.gameforge.plugin.ProjectPlugin"
		}

		create("codegen") {
			id = "io.github.wsyong11.gameforge.codegen"
			implementationClass = "io.github.wsyong11.gameforge.plugin.CodeGenPlugin"
		}
	}
}
