import io.github.wsyong11.gameforge.dependencies.*
import io.github.wsyong11.gameforge.plugin.codegen.dsl.*
import io.github.wsyong11.gameforge.project.artifactId
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import java.util.*
import javax.lang.model.element.Modifier

plugins {
	id("io.github.wsyong11.gameforge.codegen")
}

artifactId = "LogSystem"

dependencies {
	implementation(LOG4J2_CORE)
	annotationProcessor(LOG4J2_CORE)

	implementation(project(":Framework:Annotation"))
	implementation(project(":Framework:EnvConfig"))

	implementation(project(":Util"))

}

tasks.compileJava {
	options.compilerArgs.add("-Alog4j.graalvm.groupId=${project.group}")
	options.compilerArgs.add("-Alog4j.graalvm.artifactId=${project.artifactId}")
}

// ------------------------------------------------------------------------------------------------------------------ //

enum class LogLevel(
	val localeName: String,
) {
	TRACE("追踪"),
	DEBUG("调试"),
	INFO("信息"),
	WARN("警告"),
	ERROR("错误");

	companion object {
		fun forEach(callback: (LogLevel) -> Unit) {
			LogLevel.values().forEach(callback)
		}
	}
}

codegen {
	val jObjects = type(Objects::class)
	val jOverride = type(Override::class)
	val jClassLoader = type(ClassLoader::class)

	val jWeakReference = "java.lang.ref" withClass "WeakReference"

	val cInternal = "io.github.wsyong11.gameforge.framework.annotation" withClass "Internal"
	val cLogLevel = "io.github.wsyong11.gameforge.framework.system.log.core" withClass "LogLevel"
	val cLogManager = "io.github.wsyong11.gameforge.framework.system.log.core" withClass "LogManager"

	val cNotNull = "org.jetbrains.annotations" withClass "NotNull"
	val cNullable = "org.jetbrains.annotations" withClass "Nullable"

	val dcLogger = "io.github.wsyong11.gameforge.framework.system.log" withClass "Logger"
	val dcAbstractLogger = "io.github.wsyong11.gameforge.framework.system.log.core.logger" withClass "AbstractLogger"
	val dcLazyLoadLoggerImpl =
		"io.github.wsyong11.gameforge.framework.system.log.core.logger" withClass "LazyLoadLoggerImpl"

	fun TypeSpecDSL.generateLogLevelMethods(
		level: LogLevel,
		abstractMethod: Boolean = true,
		noEnabledMethod: Boolean = false,
		enabledMethodConfigurator: (MethodDSL.() -> Unit)? = null,
		logMethodConfigurator: (MethodDSL.(List<String>) -> Unit)? = null,
		logVarargMethodConfigurator: (MethodDSL.() -> Unit)? = null,
	) {
		val levelName = level.name.lowercase()

		if (!noEnabledMethod)
			method("is${levelName.uppercaseFirstChar()}Enabled") {
				modifier(Modifier.PUBLIC)
				if (abstractMethod) modifier(Modifier.ABSTRACT)
				returns(jBoolean)
				this.dsl(enabledMethodConfigurator)

				doc {
					-"检查${level.localeName}是否被启用"
					newLine()
					-"@return ${level.localeName}是否已被启用"
				}
			}

		for (i in -1..9) {
			method(levelName) {
				modifier(Modifier.PUBLIC)
				if (abstractMethod) modifier(Modifier.ABSTRACT)
				parameter(jString, "message") {
					annotation(cNotNull)
				}

				val argNames = mutableListOf<String>()
				if (i >= 0) {
					for (v in 0..i) {
						val name = "arg${v}"
						argNames.add(name)
						parameter(Any::class.java, name) {
							annotation(cNullable)
						}
					}
				}

				if (logMethodConfigurator != null) this.logMethodConfigurator(argNames)

				doc {
					+"输出${level.localeName}日志等级的信息"
					if (i > 0) +"，并按照参数进行格式化"
					newLine(2)
					+"@param message 日志信息"
					for (v in 0..i) {
						newLine()
						+"@param arg${v} 参数 ${v}"
					}
				}
			}
		}

		method(levelName) {
			modifier(Modifier.PUBLIC)
			if (abstractMethod) modifier(Modifier.ABSTRACT)
			parameter(jString, "message") {
				annotation(cNotNull)
			}
			parameter(Array<Any>::class.java, "args") {
				annotation(cNullable)
			}
			varargs()

			this.dsl(logVarargMethodConfigurator)

			doc {
				-"输出${level.localeName}日志等级的信息，并按照参数进行格式化"
				newLine()
				-"@param message 日志信息"
				-"@param args    参数"
			}
		}
	}

	register(dcLogger) {
		createInterface(dcLogger) {
			modifiers(Modifier.PUBLIC)
			method("getName") {
				modifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				annotation(cNotNull)
				returns(jString)
			}
			method("isLevelEnabled") {
				modifiers(Modifier.PUBLIC, Modifier.DEFAULT)
				parameter(cLogLevel, "level") {
					annotation(cNotNull)
				}
				returns(jBoolean)
				code {
					statement("\$T.requireNonNull(level, \"level is null\")", jObjects)

					+"return "
					controlFlow("switch (level)") {
						-"case TRACE -> this.isTraceEnabled();"
						-"case DEBUG -> this.isDebugEnabled();"
						-"case INFO -> this.isInfoEnabled();"
						-"case WARN -> this.isWarnEnabled();"
						-"case ERROR -> this.isErrorEnabled();"
					}
					+";"
				}
			}
			LogLevel.forEach {
				generateLogLevelMethods(it)
			}
		}
	}

	register(dcAbstractLogger) {
		createClass(dcAbstractLogger) {
			modifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
			annotation(cInternal)
			implements(dcLogger)

			method("log") {
				modifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
				parameter(cLogLevel, "level") {
					annotation(cNotNull)
				}
				parameter(jString, "message") {
					annotation(cNotNull)
				}
				parameter(Array<Any>::class.java, "args") {
					annotation(cNullable)
				}
				varargs()
			}

			LogLevel.forEach {
				val levelName = it.name.lowercase()

				generateLogLevelMethods(
					it,
					abstractMethod = false,
					noEnabledMethod = true,
					logMethodConfigurator = { argNames ->
						annotation(jOverride)
						code {
							ifBlock("!this.is${levelName.uppercaseFirstChar()}Enabled()") {
								+"return;\n"
							}
							newLine()
							format("this.log(\$T.${levelName.uppercase()}, ", cLogLevel)
							+"message"
							if (argNames.isNotEmpty())
								+", ${argNames.joinToString(", ")}"
							+");"
						}
					},
					logVarargMethodConfigurator = {
						annotation(jOverride)
						code {
							ifBlock("!this.is${levelName.uppercaseFirstChar()}Enabled()") {
								+"return;\n"
							}
							newLine()
							statement("this.log(\$T.${levelName.uppercase()}, message, args)", cLogLevel)
						}
					}
				)
			}
		}
	}

	register(dcLazyLoadLoggerImpl) {
		createClass(dcLazyLoadLoggerImpl) {
			modifiers(Modifier.PUBLIC)
			annotation(cInternal)
			implements(dcLogger)

			field(jWeakReference[jClassLoader], "tempClassLoader") {
				modifiers(Modifier.PRIVATE, Modifier.VOLATILE)
				annotation(cNullable)
			}

			field(jString, "tempName") {
				modifiers(Modifier.PRIVATE, Modifier.VOLATILE)
				annotation(cNullable)
			}

			field(dcLogger, "impl") {
				modifiers(Modifier.PRIVATE, Modifier.VOLATILE)
				annotation(cNullable)
			}

			constructor {
				parameter(jClassLoader, "classLoader") {
					annotation(cNotNull)
				}
				parameter(jString, "name") {
					annotation(cNotNull)
				}

				code {
					statement("\$T.requireNonNull(classLoader, \"classLoader is null\")", jObjects)
					statement("\$T.requireNonNull(name, \"name is null\")", jObjects)
					newLine()
					statement("this.tempClassLoader = new \$T<>(classLoader)", jWeakReference)
					statement("this.tempName = name")
					code("this.impl = null")
				}
			}

			method("measureImpl") {
				modifier(Modifier.PRIVATE)
				code {
					ifBlock("this.impl != null") {
						returns()
					}
					newLine(2)

					statement("\$T classLoader = this.getTempClassLoader()", jClassLoader)
					statement("\$T name = this.tempName", jString)
					ifBlock("classLoader == null || name == null") {
						returns()
					}
					newLine(2)

					statement("\$1T logManager = \$1T.getInstanceUnsafe(classLoader)", cLogManager)
					ifBlock("logManager == null") {
						returns()
					}
					newLine(2)

					tryBlock {
						code("this.impl = logManager.getLogger(name)")
					} finallyBlock {
						code("this.tempClassLoader = null")
						code("this.tempName = null")
					}
				}
			}

			method("getTempClassLoader") {
				modifiers(Modifier.PRIVATE)
				returns(jClassLoader)
				annotation(cNullable)
				code {
					statement("\$T classLoader = this.tempClassLoader", jWeakReference[jClassLoader])
					returns("classLoader == null ? null : classLoader.get()")
				}
			}

			method("getName") {
				modifiers(Modifier.PUBLIC)
				returns(jString)
				annotation(cNotNull)
				annotation(jOverride)
				code {
					code("this.measureImpl()")
					statement("\$T impl = this.impl", dcLogger)
					newLine()
					ifBlock("impl == null") {
						statement("\$T classLoader = this.getTempClassLoader()", jClassLoader)
						statement("\$T name = this.tempName", jString)
						ifBlock("classLoader == null || name == null") {
							returns("this.toString()")
						}
						newLine()
						returns("name")
					}
					newLine()
					returns("impl.getName()")
				}
			}

			LogLevel.forEach {
				val levelName = it.name.lowercase()

				generateLogLevelMethods(
					it,
					abstractMethod = false,
					enabledMethodConfigurator = {
						annotation(jOverride)
						code {
							code("this.measureImpl()")
							statement("\$T impl = this.impl", dcLogger)
							ifBlock("impl == null") {
								returns("false")
							}
							newLine()
							returns("impl.is${levelName.uppercaseFirstChar()}Enabled()")
						}
					},
					logMethodConfigurator = { argNames ->
						annotation(jOverride)
						code {
							code("this.measureImpl()")
							statement("\$T impl = this.impl", dcLogger)
							ifBlock("impl == null") {
								returns()
							}
							newLine()
							+"impl.${levelName}(message"
							if (argNames.isNotEmpty())
								+", ${argNames.joinToString(", ")}"
							+");"
						}
					},
					logVarargMethodConfigurator = {
						annotation(jOverride)
						code {
							code("this.measureImpl()")
							statement("\$T impl = this.impl", dcLogger)
							ifBlock("impl == null") {
								returns()
							}
							newLine()
							code("impl.${levelName}(message, args)")
						}
					}
				)

			}

			method("toString") {
				modifiers(Modifier.PUBLIC)
				annotation(cNotNull)
				annotation(jOverride)
				returns(jString)
				code {
					statement("\$T impl = this.impl", dcLogger)
					ifBlock("impl != null") {
						returns("impl.toString()")
					}
					newLine(2)

					statement("\$T classLoader = this.getTempClassLoader()", jClassLoader)
					statement("\$T name = this.tempName", jString)
					ifBlock("classLoader == null || name == null") {
						returns("\"LazyLoadLogger(<unknown>, <unknown>)\"")
					}
					newLine(2)
					returns(""""LazyLoadLogger(" + classLoader + ", \"" + name + "\")"""")
				}
			}

			method("equals") {
				modifiers(Modifier.PUBLIC)
				annotation(jOverride)
				returns(jBoolean)
				parameter(jObject, "o") {
					annotation(cNullable)
				}
				code {
					ifBlock("this == o") {
						returns("true")
					}
					newLine()
					ifBlock("!(o instanceof \$T that)", dcLogger) {
						returns("false")
					}
					newLine(2)
					returns("\$T.equals(this.getName(), that.getName())", jObjects)
				}
			}

			method("hashCode") {
				modifiers(Modifier.PUBLIC)
				annotation(jOverride)
				returns(jInt)
				code {
					returns("\$T.hashCode(this.getName())", jObjects)
				}
			}
		}
	}
}
