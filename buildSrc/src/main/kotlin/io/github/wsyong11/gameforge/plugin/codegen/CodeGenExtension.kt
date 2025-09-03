package io.github.wsyong11.gameforge.plugin.codegen

import io.github.wsyong11.gameforge.plugin.codegen.dsl.TypeSpecDSL
import io.github.wsyong11.gameforge.plugin.codegen.dsl.dsl
import com.palantir.javapoet.*
import kotlin.reflect.KClass

open class CodeGenExtension {
	val jObject = type(Any::class)

	val jString = type(String::class)
	val jVoid = TypeName.VOID
	val jBoolean = TypeName.BOOLEAN
	val jByte = TypeName.BYTE
	val jShort = TypeName.SHORT
	val jInt = TypeName.INT
	val jLong = TypeName.LONG
	val jChar = TypeName.CHAR
	val jFloat = TypeName.FLOAT
	val jDouble = TypeName.DOUBLE

	private val _generators: MutableList<CodeGenerator> = mutableListOf()

	public val generators
		get() = this._generators.toList()

	fun register(generator: CodeGenerator) {
		this._generators.add(generator)
	}

	fun register(
		packageName: String,
		fileConfigurator: ((JavaFile.Builder) -> Unit)? = null,
		generator: () -> TypeSpec,
	) {
		this.register {
			JavaFile.builder(packageName, generator())
				.addFileComment("!!Generate by build system!! //")
				.indent("\t")
				.apply { fileConfigurator?.invoke(this) }
				.build()
		}
	}

	fun register(
		packageName: ClassName,
		fileConfigurator: ((JavaFile.Builder) -> Unit)? = null,
		generator: () -> TypeSpec,
	) = this.register(packageName.packageName(), fileConfigurator, generator)

	fun type(type: KClass<*>) =
		ClassName.get(type.java)

	inline fun <reified T> type() =
		type(T::class)

	fun primitive(type: KClass<*>) =
		TypeName.get(type.java)

	inline fun<reified  T> primitive() =
		primitive(T::class)

	infix fun String.withClass(name: String) =
		ClassName.get(this, name)

	operator fun ClassName.get(vararg type: TypeName) =
		ParameterizedTypeName.get(this, *type)

	fun createInterface(name: String, block: TypeSpecDSL.() -> Unit) =
		TypeSpecDSL(TypeSpec.interfaceBuilder(name)).dsl(block).build()

	fun createInterface(name: ClassName, block: TypeSpecDSL.() -> Unit) =
		createInterface(name.simpleName(), block)

	fun createClass(name: String, block: TypeSpecDSL.() -> Unit) =
		TypeSpecDSL(TypeSpec.classBuilder(name)).dsl(block).build()

	fun createClass(name: ClassName, block: TypeSpecDSL.() -> Unit) =
		createClass(name.simpleName(), block)
}
