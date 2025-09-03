package io.github.wsyong11.gameforge.plugin.codegen.dsl

fun <T> T.dsl(block: (T.() -> Unit)?): T {
	if (block != null) this.block()
	return this
}