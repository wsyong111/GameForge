package io.github.wsyong11.gameforge.plugin.codegen.dsl

import com.palantir.javapoet.*
import javax.lang.model.element.Modifier

@JavaPoetMarker
class TypeSpecDSL(
	private val builder: TypeSpec.Builder,
) {
	fun modifier(modifier: Modifier) =
		this.builder.addModifiers(modifier)

	fun modifiers(vararg modifiers: Modifier) =
		this.builder.addModifiers(*modifiers)

	fun annotation(type: ClassName) =
		this.builder.addAnnotation(type)

	fun extends(type: TypeName) =
		this.builder.superclass(type)

	fun implements(type: TypeName) =
		this.builder.addSuperinterface(type)

	fun field(type: TypeName, name: String, block: (FieldDSL.() -> Unit)? = null) =
		this.builder.addField(FieldDSL(FieldSpec.builder(type, name)).dsl(block).build())

	fun method(name: String, block: MethodDSL.() -> Unit) =
		this.builder.addMethod(MethodDSL(MethodSpec.methodBuilder(name)).dsl(block).build())

	fun constructor(block: MethodDSL.() -> Unit) =
		this.builder.addMethod(MethodDSL(MethodSpec.constructorBuilder()).dsl(block).build())

	fun build() =
		this.builder.build()
}