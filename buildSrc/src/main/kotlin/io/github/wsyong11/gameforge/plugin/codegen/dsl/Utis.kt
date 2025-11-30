package io.github.wsyong11.gameforge.plugin.codegen.dsl

import com.palantir.javapoet.TypeName

fun TypeName.boxSafe(): TypeName {
    if (this.isPrimitive)
        return this.box()
    return this
}

fun TypeName.unboxSafe(): TypeName {
    if (this.isBoxedPrimitive)
        return this.unbox()
    return this
}
