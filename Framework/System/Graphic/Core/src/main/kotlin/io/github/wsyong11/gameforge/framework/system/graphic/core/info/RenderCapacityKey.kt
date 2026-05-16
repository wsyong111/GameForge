package io.github.wsyong11.gameforge.framework.system.graphic.core.info

data class RenderCapacityKey<T>(
	val key: String,
	val type: Class<T>
) {
	companion object {
		inline fun <reified T> of(key: String) =
			RenderCapacityKey(key, T::class.java)

		@JvmStatic
		fun <T> of(key: String, type: Class<T>) =
			RenderCapacityKey(key, type)
	}
}
