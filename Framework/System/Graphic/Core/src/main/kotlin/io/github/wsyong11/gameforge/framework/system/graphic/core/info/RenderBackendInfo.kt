package io.github.wsyong11.gameforge.framework.system.graphic.core.info

import org.semver4j.Semver

interface RenderBackendInfo {
	val vendor: String
	val version: Semver
	val apiVersion: String

	val allCapacity: Set<RenderCapacityKey<*>>

	fun <T> getCapacity(key: RenderCapacityKey<T>): T?
}