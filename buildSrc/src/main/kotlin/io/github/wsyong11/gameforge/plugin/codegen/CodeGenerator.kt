package io.github.wsyong11.gameforge.plugin.codegen

import com.palantir.javapoet.JavaFile

fun interface CodeGenerator {
	fun generate(): JavaFile
}