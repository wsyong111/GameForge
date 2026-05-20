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
		this._dependency =
			this._dependency.copy(additionDependency = this._dependency.additionDependency + (type to dependency))
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

infix fun Dependency.addImpl(dependency: Iterable<Dependency>): Dependency {
	var current = this
	dependency.forEach {
		current = current.addImpl(it)
	}
	return current
}

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
