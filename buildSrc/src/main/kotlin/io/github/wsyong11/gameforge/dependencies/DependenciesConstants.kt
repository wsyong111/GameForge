package io.github.wsyong11.gameforge.dependencies

//@formatter:off
// Annotation / Codegen / Utils
val SEMVER4J = "org.semver4j" dependsOn "semver4j" version "6.0.0"

val AUTO_SERVICE = "com.google.auto.service" dependsOn "auto-service" version "1.1.1"
val AUTO_COMMON  = "com.google.auto"         dependsOn "auto-common"  version "1.2.2"
val JAVA_POET    = "com.palantir.javapoet"   dependsOn "javapoet"     version "0.5.0"
val LOMBOK       = "org.projectlombok"       dependsOn "lombok"       version "1.18.38"

val ANNOTATIONS = "org.jetbrains"        dependsOn "annotations" version "26.0.2"
val GEANTYREF   = "io.leangen.geantyref" dependsOn "geantyref"   version "2.0.1"


// Apache Commons & Friends
val COMMONS_CLI         = "commons-cli"        dependsOn "commons-cli"          version "1.10.0"
val COMMONS_COLLECTIONS = "org.apache.commons" dependsOn "commons-collections4" version "4.5.0"
val COMMONS_LANG        = "org.apache.commons" dependsOn "commons-lang3"        version "3.17.0"
val COMMONS_TEXT        = "org.apache.commons" dependsOn "commons-text"         version "1.14.0"

val GUAVA     = "com.google.guava" dependsOn "guava"    version "3.4.8-jre"
val FAST_UTIL = "fastutil"         dependsOn "fastutil" version "5.0.9"


// Multi threading
val RX_JAVA = "io.reactivex.rxjava3" dependsOn "rxjava" version "3.1.12"

// Logging / Console
val LOG4J2_CORE = "org.apache.logging.log4j" dependsOn "log4j-core" version "2.25.1"
val JANSI       = "org.fusesource.jansi"     dependsOn "jansi"      version "2.4.2"
val JLINE       = "org.jline"                dependsOn "jline"      version "3.30.5"


// Math / Native Stuff
val JOML = "org.joml" dependsOn "joml" version "1.10.8"


// LWJGL (BOM + Natives)
val LWJGL_BOM = "org.lwjgl" dependsOn "lwjgl-bom" version "3.3.6" type DependencyType.BOM

private val lwjglNatives = listOf("natives-windows", "natives-linux", "natives-macos")
private fun generateLwjglNativeDependences(name: String) =
	lwjglNatives.map {
		"org.lwjgl" dependsOn "${name}::${it}" addImpl LWJGL_BOM
	}

val LWJGL_NATIVE          = generateLwjglNativeDependences("lwjgl");
val LWJGL_ASSIMP_NATIVE   = generateLwjglNativeDependences("lwjgl-assimp");
val LWJGL_GLFW_NATIVE     = generateLwjglNativeDependences("lwjgl-glfw");
val LWJGL_HARFBUZZ_NATIVE = generateLwjglNativeDependences("lwjgl-harfbuzz");
val LWJGL_MEOW_NATIVE     = generateLwjglNativeDependences("lwjgl-meow");
val LWJGL_NFD_NATIVE      = generateLwjglNativeDependences("lwjgl-nfd");
val LWJGL_OPENAL_NATIVE   = generateLwjglNativeDependences("lwjgl-openal");
val LWJGL_OPENGL_NATIVE   = generateLwjglNativeDependences("lwjgl-opengl");
val LWJGL_STB_NATIVE      = generateLwjglNativeDependences("lwjgl-stb");

val LWJGL          = "org.lwjgl" dependsOn "lwjgl"          addImpl LWJGL_BOM addImpl LWJGL_NATIVE
val LWJGL_ASSIMP   = "org.lwjgl" dependsOn "lwjgl-assimp"   addImpl LWJGL_BOM addImpl LWJGL_NATIVE addImpl LWJGL_ASSIMP_NATIVE
val LWJGL_FMOD     = "org.lwjgl" dependsOn "lwjgl-fmod"     addImpl LWJGL_BOM addImpl LWJGL_NATIVE
val LWJGL_GLFW     = "org.lwjgl" dependsOn "lwjgl-glfw"     addImpl LWJGL_BOM addImpl LWJGL_NATIVE addImpl LWJGL_GLFW_NATIVE
val LWJGL_HARFBUZZ = "org.lwjgl" dependsOn "lwjgl-harfbuzz" addImpl LWJGL_BOM addImpl LWJGL_NATIVE addImpl LWJGL_HARFBUZZ_NATIVE
val LWJGL_MEOW     = "org.lwjgl" dependsOn "lwjgl-meow"     addImpl LWJGL_BOM addImpl LWJGL_NATIVE addImpl LWJGL_MEOW_NATIVE
val LWJGL_NFD      = "org.lwjgl" dependsOn "lwjgl-nfd"      addImpl LWJGL_BOM addImpl LWJGL_NATIVE addImpl LWJGL_NFD_NATIVE
val LWJGL_OPENAL   = "org.lwjgl" dependsOn "lwjgl-openal"   addImpl LWJGL_BOM addImpl LWJGL_NATIVE addImpl LWJGL_OPENAL_NATIVE
val LWJGL_OPENGL   = "org.lwjgl" dependsOn "lwjgl-opengl"   addImpl LWJGL_BOM addImpl LWJGL_NATIVE addImpl LWJGL_OPENGL_NATIVE
val LWJGL_STB      = "org.lwjgl" dependsOn "lwjgl-stb"      addImpl LWJGL_BOM addImpl LWJGL_NATIVE addImpl LWJGL_STB_NATIVE


// Serialization / JSON / Config
val JACKSON      = "com.fasterxml.jackson.core"       dependsOn "jackson-databind"        version "2.19.2"
val JACKSON_YAML = "com.fasterxml.jackson.dataformat" dependsOn "jackson-dataformat-yaml" version JACKSON
val JACKSON_TOML = "com.fasterxml.jackson.dataformat" dependsOn "jackson-dataformat-toml" version JACKSON
val JACKSON_XML  = "com.fasterxml.jackson.dataformat" dependsOn "jackson-dataformat-xml"  version JACKSON

val GSON = "com.google.code.gson" dependsOn "gson" version "2.13.2"

val JSON_PATH = ("com.jayway.jsonpath" dependsOn "json-path" version "2.9.0") {
	exclude("org.slf4j" dependsOn "slf4j-api")
}


// System / Hardware Info
val OSHI = "com.github.oshi" dependsOn "oshi-core" version "6.9.1"

val JNA_PLATFORM = "net.java.dev.jna" dependsOn "jna-platform" version "5.18.1"
val JNA = "net.java.dev.jna" dependsOn "jna" version JNA_PLATFORM addImpl JNA_PLATFORM

//@formatter:on
