package io.github.wsyong11.gameforge.plugin.codegen.dsl

import com.palantir.javapoet.ClassName
import com.palantir.javapoet.MethodSpec
import com.palantir.javapoet.ParameterSpec
import com.palantir.javapoet.TypeName
import java.lang.reflect.Type
import javax.lang.model.element.Modifier

@JavaPoetMarker
class MethodDSL(
	private val builder: MethodSpec.Builder,
) {
	fun doc(block: CodeDSL.() -> Unit) =
		this.builder.addJavadoc(CodeDSL().dsl(block).build())

	fun modifier(modifier: Modifier) =
		this.builder.addModifiers(modifier)

	fun modifiers(vararg modifiers: Modifier) =
		this.builder.addModifiers(*modifiers)

	fun annotation(type: ClassName) =
		this.builder.addAnnotation(type)

	fun annotation(type: Class<*>) =
		this.builder.addAnnotation(type)

	fun returns(type: Type) =
		this.builder.returns(type)

	fun returns(type: TypeName) =
		this.builder.returns(type)

	fun parameter(type: TypeName, name: String, block: (ParameterDSL.() -> Unit)? = null) =
		this.builder.addParameter(ParameterDSL(ParameterSpec.builder(type, name)).dsl(block).build())

	fun parameter(type: Type, name: String, block: (ParameterDSL.() -> Unit)? = null) =
		this.builder.addParameter(ParameterDSL(ParameterSpec.builder(type, name)).dsl(block).build())

	fun varargs(enable: Boolean = true) =
		this.builder.varargs(enable)

	fun code(block: CodeDSL.() -> Unit) =
		this.builder.addCode(CodeDSL().dsl(block).build())

	fun build() =
		this.builder.build()
}
