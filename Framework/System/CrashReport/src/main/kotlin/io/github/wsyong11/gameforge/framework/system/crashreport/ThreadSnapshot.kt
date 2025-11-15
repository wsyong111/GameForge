package io.github.wsyong11.gameforge.framework.system.crashreport

import java.lang.management.ThreadInfo

data class ThreadSnapshot(
	val name: String,
	val id: Long,
	val priority: Int,
	@get:JvmName("isDaemon")
	val daemon: Boolean,
	val state: Thread.State,
	val stackTrace: List<StackTraceElement>,
	@get:JvmName("isInNative")
	val inNative: Boolean,
) {
	companion object {
		@JvmStatic
		fun of(info: ThreadInfo) =
			ThreadSnapshot(
				name = info.threadName,
				id = info.threadId,
				priority = info.priority,
				daemon = info.isDaemon,
				state = info.threadState,
				stackTrace = info.stackTrace.toList(),
				inNative = info.isInNative
			)
	}
}
