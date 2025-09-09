package io.github.wsyong11.gameforge.plugin.codegen.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.konan.file.File

abstract class CodeGenTask : DefaultTask() {
	@get:Input
	abstract val sourceCode: MapProperty<String, String>

	@get:OutputDirectory
	abstract val outputDir: DirectoryProperty

	@TaskAction
	fun generateCode() {
		val dir = this.outputDir.get().asFile

		this.sourceCode.getOrElse(emptyMap()).forEach({ className, sourceCode ->
			val filePath = className.replace('.', File.separatorChar) + ".java"
			val file = dir.resolve(filePath)
			file.parentFile.mkdirs()
			file.writeText(sourceCode)
		})
	}
}