@file:JvmSynthetic

package io.github.wsyong11.gameforge.framework

import io.github.wsyong11.gameforge.util.dsl

class ColorBuilder {
	var r: Int = 0
	var rf: Float
		get() = this.r / 255.0F
		set(v) {
			this.r = (v * 255.0F).toInt()
		}

	var g: Int = 0
	var gf: Float
		get() = this.g / 255.0F;
		set(v) {
			this.g = (v * 255.0F).toInt()
		}

	var b: Int = 0
	var bf: Float
		get() = this.b / 255.0F;
		set(v) {
			this.b = (v * 255.0F).toInt()
		}

	var a: Int = 255
	var af: Float
		get() = this.a / 255.0F;
		set(v) {
			this.a = (v * 255.0F).toInt()
		}

	fun build() = Color.of(this.a, this.r, this.g, this.b)
}

fun color(block: ColorBuilder.() -> Unit) =
	dsl(ColorBuilder(), block, ColorBuilder::build)
