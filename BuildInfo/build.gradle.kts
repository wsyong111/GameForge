import io.github.wsyong11.gameforge.project.includeDebug
import java.time.LocalDateTime
import javax.lang.model.element.Modifier

plugins {
	id("io.github.wsyong11.gameforge.codegen")
}

codegen {
	val dcBuildInfo = "io.github.wsyong11.gameforge" withClass "BuildInfo"

	register(dcBuildInfo) {
		createClass(dcBuildInfo) {
			modifiers(Modifier.PUBLIC, Modifier.FINAL)

			constructor {
				modifiers(Modifier.PRIVATE)
				code {
					+"/* no-op */"
				}
			}

			field(jString, "BUILD_TIME") {
				modifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				initializer("\"${LocalDateTime.now()}\"")
			}
			field(jString, "VERSION") {
				modifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				initializer("\"${rootProject.version}\"")
			}
			field(jBoolean, "DEBUG") {
				modifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				initializer("${includeDebug}")
			}
		}
	}
}