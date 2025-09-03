package io.github.wsyong11.gameforge.plugin.codegen.dsl

import com.palantir.javapoet.ClassName
import com.palantir.javapoet.FieldSpec
import javax.lang.model.element.Modifier

@JavaPoetMarker
class FieldDSL(
	private val builder: FieldSpec.Builder,
) {

	fun modifier(modifier: Modifier) =
		this.builder.addModifiers(modifier)

	fun modifiers(vararg modifiers: Modifier) =
		this.builder.addModifiers(*modifiers)

	fun annotation(type: ClassName) =
		this.builder.addAnnotation(type)

	fun initializer(code: String, vararg args: Any) =
		this.builder.initializer(code, *args)

	fun initializer(code: CodeDSL.() -> Unit) =
		this.builder.initializer(CodeDSL().dsl(code).build())

	fun build() =
		this.builder.build()
}