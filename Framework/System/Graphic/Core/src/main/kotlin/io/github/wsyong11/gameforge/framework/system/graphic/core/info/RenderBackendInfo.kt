package io.github.wsyong11.gameforge.framework.system.graphic.core.info

import org.semver4j.Semver

interface RenderBackendInfo {
	val driver: String
	val version: Semver
	val vendor: String
	val device: String?

	val allCapacity: Set<RenderCapacityKey<*>>

	fun <T> getCapacity(key: RenderCapacityKey<T>): T?

	fun hasCapacity(key: RenderCapacityKey<*>): Boolean
}