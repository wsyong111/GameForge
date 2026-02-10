package io.github.wsyong11.gameforge.util.debug

import java.lang.management.ThreadInfo

data class ThreadSnapshot(
	val name: String,
	val id: Long,
	val priority: Int,
	@get:JvmName("isDaemon")
	val daemon: Boolean,
	val state: Thread.State,
	val stackTrace: List<StackTraceElement>,
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
				stackTrace = info.stackTrace.toList()
			)

		@JvmStatic
		fun snapshot(thread: Thread) =
			ThreadSnapshot(
				name = thread.name,
				id = thread.id,
				priority = thread.priority,
				daemon = thread.isDaemon,
				state = thread.state,
				stackTrace = thread.stackTrace.toList()
			)
	}

	override fun toString(): String {
		return ("ThreadSnapshot{" +
				"name=${this.name}, " +
				"id=${this.id}, " +
				"priority=${this.priority}, " +
				"daemon=${this.daemon}, " +
				"state=${this.state}, " +
				"stackTrace=${this.stackTrace}}")
	}
}
