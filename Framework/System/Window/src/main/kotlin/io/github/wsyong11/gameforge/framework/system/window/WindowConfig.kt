package io.github.wsyong11.gameforge.framework.system.window

import io.github.wsyong11.gameforge.framework.Identifier
import org.joml.Vector2ic

data class WindowConfig(
	val title: String,
	val size: Vector2ic,
	val position: Vector2ic?,
	val displayType: WindowDisplayType,
	val vsync: VSyncType,

	val api: Identifier,
	val graphicsConfig: WindowGraphicsConfig,

    val backendHints: Map<String, Any>
)
