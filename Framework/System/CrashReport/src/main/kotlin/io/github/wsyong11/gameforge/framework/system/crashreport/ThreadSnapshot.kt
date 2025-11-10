package io.github.wsyong11.gameforge.framework.system.crashreport

data class ThreadSnapshot(
    val name:String,
    val id:Long,
    val priority: Int,
    val state: Thread.State,
    val stackTrace: List<StackTraceElement>,
)
