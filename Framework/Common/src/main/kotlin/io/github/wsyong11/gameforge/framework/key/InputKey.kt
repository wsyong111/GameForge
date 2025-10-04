package io.github.wsyong11.gameforge.framework.key

import java.util.concurrent.ConcurrentHashMap

abstract class InputKey protected constructor() {
	data class Keyboard internal constructor(override val value: KeyCode) : InputKey()

	data class Mouse internal constructor(override val value: MouseButton) : InputKey()


	protected abstract val value: Any

	override fun toString(): String =
		"${this.javaClass.simpleName}[${this.value}]"

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (this.javaClass != other?.javaClass) return false

		other as InputKey
		return this.value == other.value
	}

	override fun hashCode() =
		this.value.hashCode()


	companion object {
		private val INSTANCES: MutableMap<Class<*>, MutableMap<Any, Any>> = ConcurrentHashMap()

		@Suppress("UNCHECKED_CAST")
		private inline fun <reified T, I> getInstanceMap() =
			INSTANCES.computeIfAbsent(T::class.java) { ConcurrentHashMap() } as MutableMap<T, I>

		private inline fun <reified T, I> get(value: T, noinline factory: (T) -> I) =
			getInstanceMap<T, I>().computeIfAbsent(value, factory)

		@JvmStatic
		fun of(key: KeyCode): Keyboard = get(key, ::Keyboard)

		@JvmStatic
		fun of(key: MouseButton): Mouse = get(key, ::Mouse)
	}
}