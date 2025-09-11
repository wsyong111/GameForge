package io.github.wsyong11.gameforge.framework.system.window

import org.joml.Vector2ic

data class WindowConfig(
	val size: Vector2ic,
	val position: Vector2ic?,
	val flags: Map<Int, Int>
)
