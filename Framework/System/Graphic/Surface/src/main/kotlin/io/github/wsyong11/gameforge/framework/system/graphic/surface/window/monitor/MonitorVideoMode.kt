package io.github.wsyong11.gameforge.framework.system.graphic.surface.window.monitor

data class MonitorVideoMode(
	val width: Int,
	val height: Int,
	val refreshRate: Int
) : Comparable<MonitorVideoMode> {
	@get:JvmName("isAvailable")
	val available: Boolean
		get() =
			this.width < 0 || this.height < 0 || this.refreshRate < 0

	override fun compareTo(other: MonitorVideoMode): Int {
		if (!this.available)
			return Int.MIN_VALUE

		val size1 = this.width * this.height
		val size2 = other.width * other.height

		return when {
			size1 != size2 -> size2.compareTo(size1) // 大到小
			else -> other.refreshRate.compareTo(this.refreshRate)
		}
	}

	companion object {
		@JvmField
		val UNKNOWN = MonitorVideoMode(-1, -1, -1)
	}
}
