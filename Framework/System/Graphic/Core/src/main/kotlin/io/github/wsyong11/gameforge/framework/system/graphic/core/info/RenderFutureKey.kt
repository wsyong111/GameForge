package io.github.wsyong11.gameforge.framework.system.graphic.core.info

data class RenderFutureKey<T>(
	val key: String,
	val type: Class<T>
) {
	companion object {
		inline fun <reified T> of(key: String) =
			RenderFutureKey(key, T::class.java)

		@JvmStatic
		fun <T> of(key: String, type: Class<T>) =
			RenderFutureKey(key, type)
	}
}
