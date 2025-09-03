package io.github.wsyong11.gameforge.util

fun <T> T.requireNonNull(message: String) {
	if (this == null)
		throw NullPointerException(message)
}