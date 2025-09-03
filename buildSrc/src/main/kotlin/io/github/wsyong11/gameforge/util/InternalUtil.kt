package io.github.wsyong11.gameforge.util

internal fun String.appendIfNotNull(value: String?, prefix: String = "", stffux: String = "") =
	if (value == null) this else this + prefix + value + stffux


