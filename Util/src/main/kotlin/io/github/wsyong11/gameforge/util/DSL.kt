@file:JvmSynthetic
package io.github.wsyong11.gameforge.util

inline fun <B, T> dsl(
    builder: B,
    block: B.() -> Unit,
    build: B.() -> T
): T = builder.apply(block).build()
