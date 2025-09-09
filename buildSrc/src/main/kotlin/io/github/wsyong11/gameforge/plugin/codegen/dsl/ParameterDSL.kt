package io.github.wsyong11.gameforge.plugin.codegen.dsl

import com.palantir.javapoet.ClassName
import com.palantir.javapoet.ParameterSpec

@JavaPoetMarker
class ParameterDSL(
	private val builder: ParameterSpec.Builder,
) {
	fun annotation(type: ClassName) =
		this.builder.addAnnotation(type)

	fun build() = this.builder.build()
}