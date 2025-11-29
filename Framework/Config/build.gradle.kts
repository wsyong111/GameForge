import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import javax.lang.model.element.Modifier

plugins {
	id("io.github.wsyong11.gameforge.codegen")
}

codegen {
	val dcConfigAccessor = "io.github.wsyong11.gameforge.framework.config" withClass "ConfigAccessor"

	val dataTypes = listOf(
		"boolean" to jBoolean
	)

	register(dcConfigAccessor) {
		createInterface(dcConfigAccessor) {
			modifiers(Modifier.PUBLIC)

			for (type in dataTypes) {
				method("get${type.first.uppercaseFirstChar()}"){
					modifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
					returns(type.second)
				}
			}
		}
	}
}
