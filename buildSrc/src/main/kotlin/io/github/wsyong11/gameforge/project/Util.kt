package io.github.wsyong11.gameforge.project

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.extraProperties
import kotlin.reflect.KProperty

val Project.visual: Boolean
    get() {
        return !this.buildFile.exists()
    }

fun Project.getOutputDir(name: String) =
    this.layout.buildDirectory.dir("generated/${name}").get()

fun Project.getOutputFile(name: String) =
    this.layout.buildDirectory.file("generated/${name}").get()

private object NullObj

operator fun Project.get(property: String): Any? {
	if (!this.hasProperty(property)) {
		if (!this.extraProperties.has(property))
			return null
		return this.extraProperties.get(property)
	}

    val propertyValue = this.property(property)
    return if (propertyValue === NullObj) null else propertyValue
}

operator fun Project.set(property: String, value: Any?) =
	this.extraProperties.set(property, value ?: NullObj)


@Suppress("UNCHECKED_CAST")
class ProjectConfig<T> {
    operator fun getValue(thisRef: Project, property: KProperty<*>): T {
        return thisRef[property.name] as T
    }

    operator fun setValue(thisRef: Project, property: KProperty<*>, value: T) {
        thisRef[property.name] = value
    }
}
