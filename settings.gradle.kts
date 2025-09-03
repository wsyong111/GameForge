pluginManagement {
    plugins {
        kotlin("jvm") version "2.0.0" apply false

        id("com.gradleup.shadow") version "9.0.0-beta15" apply false
    }

    repositories {
        gradlePluginPortal()
    }
}

rootProject.name = "GameForge"

include(":AnnotationProcessor")
include(":BuildInfo")
include(":Util")

include(":Framework")
include(":Framework:Annotation")
include(":Framework:Application")
include(":Framework:Bootstrap")
include(":Framework:Common")
include(":Framework:Config")
include(":Framework:DataFlow")
include(":Framework:Debug")
include(":Framework:EnvConfig")
include(":Framework:Event")
include(":Framework:Lang")
include(":Framework:Lifecycle")
include(":Framework:Listener")
include(":Framework:RichText")
//include(":Framework:System:CrashReport")
include(":Framework:System:Log")
include(":Framework:System:Render")
include(":Framework:System:Resource")
include(":Framework:System:Security")

include(":Game")
include(":Game:Core")
include(":Game:Core:ClientCore")
include(":Game:Core:CommonCore")
include(":Game:Core:ServerCore")
include(":Game:Client")
include(":Game:Common")
include(":Game:Server")
include(":Game:Loader")
include(":Game:Mod")
//include(":Game:Mod:Loader")
//include(":Game:Mod:Management")
//include(":Game:Mod:Runtime")
