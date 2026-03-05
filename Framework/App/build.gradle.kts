plugins{
	kotlin("jvm")
}

dependencies {
	implementation(project(":Framework:EnvConfig"))
	implementation(project(":Framework:Context"))
	implementation(project(":Framework:System:Log"))
}
