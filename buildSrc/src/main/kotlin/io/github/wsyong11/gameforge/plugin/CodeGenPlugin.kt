package io.github.wsyong11.gameforge.plugin

import io.github.wsyong11.gameforge.plugin.codegen.CodeGenExtension
import io.github.wsyong11.gameforge.plugin.codegen.task.CodeGenTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension

class CodeGenPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.pluginManager.apply("java")

		val ext = project.extensions.create("codegen", CodeGenExtension::class.java)

		project.afterEvaluate {
			val javaExt = project.extensions.getByType(JavaPluginExtension::class.java)
			setupCodeGenTask(project, ext, javaExt)
		}
	}

	private fun setupCodeGenTask(project: Project, ext: CodeGenExtension, javaExt: JavaPluginExtension) {
		val outputDirLocation = project.layout.buildDirectory.dir("generated/codegen").get()

		val sourceCodes = mutableMapOf<String, String>()

		ext.generators
			.map {
				try {
					it.generate()
				} catch (e: Exception) {
					throw GradleException("Code generator error \n${e.stackTraceToString()}")
				}
			}
			.forEach {
				val packageName = it.packageName()
				val name = it.typeSpec().name()

				sourceCodes["${packageName}.${name}"] = it.toString()
			}

		val codeGenTask = project.tasks.register("javaCodeGen", CodeGenTask::class.java) {
			this.sourceCode.set(sourceCodes)
			this.outputDir.set(outputDirLocation)
		}

		project.tasks.named("compileJava").configure {
			dependsOn(codeGenTask)
		}

//		project.tasks.named("processResources").configure { dependsOn(generateTask) }

		javaExt.sourceSets.getByName("main").java.srcDir(outputDirLocation)
	}
}