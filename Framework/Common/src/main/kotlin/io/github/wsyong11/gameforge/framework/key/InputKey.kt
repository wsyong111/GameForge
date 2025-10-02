package io.github.wsyong11.gameforge.framework.key

import java.util.concurrent.ConcurrentHashMap

abstract class InputKey {
	data class Keyboard(override val value: KeyCode) : InputKey()

	data class Mouse(override val value: MouseButton) : InputKey()


	protected abstract val value: Any

	override fun toString(): String =
		"${this.javaClass.simpleName}[${this.value}]"

	override fun equals(other: Any?) =
		this.value == other

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