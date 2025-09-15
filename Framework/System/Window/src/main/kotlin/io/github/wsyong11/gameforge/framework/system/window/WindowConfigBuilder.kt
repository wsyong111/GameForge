package io.github.wsyong11.gameforge.framework.system.window

import io.github.wsyong11.gameforge.framework.Identifier
import io.github.wsyong11.gameforge.util.requireNonNull
import org.joml.Vector2i
import org.joml.Vector2ic

class WindowConfigBuilder {
	private var title = ""
	private var size: Vector2ic = Vector2i()
	private var position: Vector2ic? = null
	private var displayType = WindowDisplayType.WINDOW
	private var api: Identifier? = null
	private var graphicsConfig: WindowGraphicsConfig? = null

	private val backendHints = mutableMapOf<String, Any>()

	fun title(title: String): WindowConfigBuilder {
		this.title = title
		return this
	}

	fun size(size: Vector2ic): WindowConfigBuilder {
		this.size = size
		return this
	}

	fun position(position: Vector2ic): WindowConfigBuilder {
		this.position = position
		return this
	}

	fun displayType(displayType: WindowDisplayType): WindowConfigBuilder {
		this.displayType = displayType
		return this
	}

	fun api(api: Identifier): WindowConfigBuilder {
		this.api = api
		return this
	}

	fun graphicsConfig(graphicsConfig: WindowGraphicsConfig): WindowConfigBuilder {
		this.graphicsConfig = graphicsConfig
		return this
	}

	fun hint(key: String, value: Any): WindowConfigBuilder {
		this.backendHints[key] = value
		return this
	}

	fun build(): WindowConfig {
		this.api.requireNonNull("Require api field")
		this.graphicsConfig.requireNonNull("Require graphicsConfig field")

		return WindowConfig(
			title = this.title,
			size = this.size,
			position = this.position,
			displayType = this.displayType,
			api = this.api!!,
			graphicsConfig = this.graphicsConfig!!,
			backendHints = this.backendHints
		)
	}
}