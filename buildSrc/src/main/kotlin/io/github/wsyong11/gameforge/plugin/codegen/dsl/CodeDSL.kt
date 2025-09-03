package io.github.wsyong11.gameforge.plugin.codegen.dsl

import com.palantir.javapoet.CodeBlock

@JavaPoetMarker
class CodeDSL {
	private val builder: CodeBlock.Builder = CodeBlock.builder()

	fun statement(statement: String, vararg args: Any) =
		this.builder.addStatement(statement, *args)

	fun format(format: String, vararg args: Any) {
		this.builder.add("$[");
		this.builder.add(format, *args);
		this.builder.add("$]");
	}

	fun add(code: String) =
		this.builder.add(code)

	fun add(code: CodeBlock) =
		this.builder.add(code)

	fun code(code: String) =
		this.add(code + ";\n")

	fun indent(block: CodeDSL.() -> Unit) {
		this.builder.indent()
		this.block()
		this.builder.unindent()
	}

	fun controlFlow(format: String, vararg args: Any, block: CodeDSL.() -> Unit) {
		this.format(format, *args)
		this.add(" {\n")
		this.indent(block)
		this.add("}")
	}

	fun newLine(count: Int = 1) {
		this.add("\n".repeat(count))
	}

	fun returns(format: String, vararg args: Any){
		this.add("return ")
		this.format(format, *args)
		this.add(";")
		this.newLine()
	}

	fun returns() {
		this.code("return")
	}

	/** if (...) { ... } */
	fun ifBlock(condition: String, vararg args: Any, block: CodeDSL.() -> Unit) {
		this.add("if (")
		this.format(condition, *args)
		this.add(") {\n")
		this.indent(block)
		this.add("} ")
	}

	/** else { ... } */
	fun elseBlock(block: CodeDSL.() -> Unit) {
		this.add("else {\n")
		this.indent(block)
		this.add("}")
	}

	infix fun Unit.elseBlock(block: CodeDSL.() -> Unit) {
		this@CodeDSL.elseBlock(block)
	}

	/** for (...) { ... } */
	fun forBlock(condition: String, vararg args: Any, block: CodeDSL.() -> Unit) {
		this.add("for (")
		this.format(condition, *args)
		this.add(") {\n")
		this.indent(block)
		this.add("}")
	}

	/** while (...) { ... } */
	fun whileBlock(condition: String, vararg args: Any, block: CodeDSL.() -> Unit) {
		this.add("while (")
		this.format(condition, *args)
		this.add(") {\n")
		this.indent(block)
		this.add("}")
	}

	/** try { ... } */
	fun tryBlock(block: CodeDSL.() -> Unit) {
		this.add("try {\n")
		this.indent(block)
		this.add("} ")
	}

	/** catch (...) { ... } */
	fun catchBlock(exception: String, vararg args: Any, block: CodeDSL.() -> Unit) {
		this.add("catch (")
		this.format(exception, *args)
		this.add(") {\n")
		this.indent(block)
		this.add("} ")
	}

	fun Unit.catchBlock(exception: String, vararg args: Any, block: CodeDSL.() -> Unit) {
		this@CodeDSL.catchBlock(exception, *args, block = block)
	}

	fun finallyBlock(block: CodeDSL.() -> Unit) {
		this.add("finally {\n")
		this.indent(block)
		this.add("}")
	}

	infix fun Unit.finallyBlock(block: CodeDSL.() -> Unit) {
		this@CodeDSL.finallyBlock(block)
	}

	fun build() =
		this.builder.build()

	operator fun String.unaryPlus() {
		this@CodeDSL.add(this)
	}
}