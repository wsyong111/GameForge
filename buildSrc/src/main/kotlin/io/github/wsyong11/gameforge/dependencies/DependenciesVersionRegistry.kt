package io.github.wsyong11.gameforge.dependencies

import io.github.wsyong11.gameforge.util.appendIfNotNull

data class Dependency(
	val group: String,
	val artifact: String,
	val version: String?,
	val type: DependencyType = DependencyType.DEPENDENCY,
	val exclude: List<Dependency> = listOf(),
	val additionDependency: List<Pair<ImportType, Dependency>> = listOf(),
) {
	override fun toString(): String {
		return "${this.group}:${this.artifact}".appendIfNotNull(this.version, prefix = ":")
	}

	companion object {
		fun of(location: String): Dependency {
			val split = location.split(":")
			return when (split.size) {
				2 -> Dependency(split[0], split[1], null)
				3 -> Dependency(split[0], split[1], split[2])
				else -> throw IllegalArgumentException("Cannot parse maven location \"${location}\"")
			}
		}

		operator fun invoke(location: String) =
			of(location)
	}
}

enum class DependencyType {
	DEPENDENCY,
	BOM
}

class DependencyDSL(
	private var _dependency: Dependency,
) {
	val dependency: Dependency
		get() = this._dependency

	fun exclude(dependency: Dependency) {
		this._dependency = this._dependency.copy(exclude = this._dependency.exclude + dependency)
	}

	fun type(type: DependencyType) {
		this._dependency = this._dependency.copy(type = type)
	}

	fun addition(type: ImportType, dependency: Dependency) {
		this._dependency = this._dependency.copy(additionDependency = this._dependency.additionDependency + (type to dependency))
	}
}

infix fun String.dependsOn(artifact: String) =
	Dependency(this, artifact, null)

infix fun Dependency.withArtifact(artifact: String) =
	this.copy(artifact = artifact)

infix fun Dependency.version(version: String?) =
	this.copy(version = version)

infix fun Dependency.version(version: Dependency) =
	this.copy(version = version.version)

infix fun Dependency.type(type: DependencyType) =
	this.copy(type = type)

infix fun Dependency.addImpl(dependency: Dependency) =
	this.copy(additionDependency = this.additionDependency + (ImportType.IMPLEMENTATION to dependency))

infix fun Dependency.addRuntime(dependency: Dependency) =
	this.copy(additionDependency = this.additionDependency + (ImportType.RUNTIME_ONLY to dependency))

infix fun Dependency.addCompile(dependency: Dependency) =
	this.copy(additionDependency = this.additionDependency + (ImportType.COMPILE_ONLY to dependency))

infix fun Dependency.withVersion(version: String?) =
	this.copy(version = version)

operator fun Dependency.invoke(config: DependencyDSL.() -> Unit): Dependency {
	val dsl = DependencyDSL(this)
	dsl.config()
	return dsl.dependency
}


//@formatter:off
val AUTO_SERVICE = "com.google.auto.service" dependsOn "auto-service" version "1.1.1"
val LOMBOK       = "org.projectlombok"       dependsOn "lombok"       version "1.18.38"

val ANNOTATIONS         = "org.jetbrains"      dependsOn "annotations"          version "26.0.2"
val COMMONS_LANG        = "org.apache.commons" dependsOn "commons-lang3"        version "3.17.0"
val COMMONS_COLLECTIONS = "org.apache.commons" dependsOn "commons-collections4" version "4.5.0"
val COMMONS_CLI         = "commons-cli"        dependsOn "commons-cli"          version "1.10.0"
val GUAVA               = "com.google.guava"   dependsOn "guava"                version "3.4.8-jre"

val GEANTYREF   = "io.leangen.geantyref"     dependsOn "geantyref"  version "2.0.1"
val JANSI       = "org.fusesource.jansi"     dependsOn "jansi"      version "2.4.2"
val JLINE       = "org.jline"                dependsOn "jline"      version "3.30.5"
val JOML        = "org.joml"                 dependsOn "joml"       version "1.10.8"
val LOG4J2_CORE = "org.apache.logging.log4j" dependsOn "log4j-core" version "2.25.1"

val LWJGL_BOM      = "org.lwjgl" dependsOn "lwjgl-bom"      version "3.3.6"   type DependencyType.BOM
val LWJGL          = "org.lwjgl" dependsOn "lwjgl"          addImpl LWJGL_BOM
val LWJGL_ASSIMP   = "org.lwjgl" dependsOn "lwjgl-assimp"   addImpl LWJGL_BOM
val LWJGL_FMOD     = "org.lwjgl" dependsOn "lwjgl-fmod"     addImpl LWJGL_BOM
val LWJGL_GLFW     = "org.lwjgl" dependsOn "lwjgl-glfw"     addImpl LWJGL_BOM
val LWJGL_HARFBUZZ = "org.lwjgl" dependsOn "lwjgl-harfbuzz" addImpl LWJGL_BOM
val LWJGL_MEOW     = "org.lwjgl" dependsOn "lwjgl-meow"     addImpl LWJGL_BOM
val LWJGL_NFD      = "org.lwjgl" dependsOn "lwjgl-nfd"      addImpl LWJGL_BOM
val LWJGL_OPENAL   = "org.lwjgl" dependsOn "lwjgl-openal"   addImpl LWJGL_BOM
val LWJGL_OPENGL   = "org.lwjgl" dependsOn "lwjgl-opengl"   addImpl LWJGL_BOM
val LWJGL_STB      = "org.lwjgl" dependsOn "lwjgl-stb"      addImpl LWJGL_BOM

val JACKSON      = "com.fasterxml.jackson.core"       dependsOn "jackson-databind"        version "2.19.2"
val JACKSON_YAML = "com.fasterxml.jackson.dataformat" dependsOn "jackson-dataformat-yaml" version JACKSON
val JACKSON_TOML = "com.fasterxml.jackson.dataformat" dependsOn "jackson-dataformat-toml" version JACKSON
val JACKSON_XML  = "com.fasterxml.jackson.dataformat" dependsOn "jackson-dataformat-xml"  version JACKSON

val JSON_PATH = ("com.jayway.jsonpath" dependsOn "json-path" version "2.9.0") {
	exclude("org.slf4j" dependsOn "slf4j-api")
}
//@formatter:on
