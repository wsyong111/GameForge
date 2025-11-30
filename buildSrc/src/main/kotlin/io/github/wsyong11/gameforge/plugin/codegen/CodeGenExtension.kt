package io.github.wsyong11.gameforge.plugin.codegen

import com.palantir.javapoet.*
import io.github.wsyong11.gameforge.plugin.codegen.dsl.TypeSpecDSL
import io.github.wsyong11.gameforge.plugin.codegen.dsl.dsl
import kotlin.reflect.KClass

open class CodeGenExtension {
    val jObject = type(Any::class)

    //@formatter:off
    val jString  : ClassName = type(String::class)
    val jVoid    : TypeName = TypeName.VOID
    val jcVoid   : TypeName = jVoid.box()
    val jBoolean : TypeName = TypeName.BOOLEAN
    val jcBoolean: TypeName = jBoolean.box()
    val jByte    : TypeName = TypeName.BYTE
    val jcByte   : TypeName = jByte.box()
    val jShort   : TypeName = TypeName.SHORT
    val jcShort  : TypeName = jShort.box()
    val jInt     : TypeName = TypeName.INT
    val jcInt    : TypeName = jInt.box()
    val jLong    : TypeName = TypeName.LONG
    val jcLong   : TypeName = jLong.box()
    val jChar    : TypeName = TypeName.CHAR
    val jcChar   : TypeName = jChar.box()
    val jFloat   : TypeName = TypeName.FLOAT
    val jcFloat  : TypeName = jFloat.box()
    val jDouble  : TypeName = TypeName.DOUBLE
    val jcDouble : TypeName = jDouble.box()
    //@formatter:on

    private val _generators: MutableList<CodeGenerator> = mutableListOf()

    val generators
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

    fun type(type: KClass<*>): ClassName =
        ClassName.get(type.java)

    fun type(type: Class<*>): ClassName =
        ClassName.get(type)

    inline fun <reified T> type() =
        type(T::class)

    fun primitive(type: KClass<*>): TypeName =
        TypeName.get(type.java)

    inline fun <reified T> primitive() =
        primitive(T::class)

    infix fun String.withClass(name: String): ClassName =
        ClassName.get(this, name)

    operator fun ClassName.get(vararg type: TypeName): ParameterizedTypeName =
        ParameterizedTypeName.get(this, *type)

    fun createInterface(name: String, block: TypeSpecDSL.() -> Unit): TypeSpec =
        TypeSpecDSL(TypeSpec.interfaceBuilder(name)).dsl(block).build()

    fun createInterface(name: ClassName, block: TypeSpecDSL.() -> Unit) =
        createInterface(name.simpleName(), block)

    fun createClass(name: String, block: TypeSpecDSL.() -> Unit): TypeSpec =
        TypeSpecDSL(TypeSpec.classBuilder(name)).dsl(block).build()

    fun createClass(name: ClassName, block: TypeSpecDSL.() -> Unit) =
        createClass(name.simpleName(), block)
}
