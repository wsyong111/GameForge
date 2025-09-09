package io.github.wsyong11.gameforge.framework.text.rich

import io.github.wsyong11.gameforge.framework.Color

class TextStyleBuilder {
	var color: Color = Color.BLACK
	private var modifier: Int = 0

	var bold: Boolean
		get() = TextStyleModifier.isBold(this.modifier)
		set(value) = this.setModifier(TextStyleModifier.BOLD, value)

	var italic: Boolean
		get() = TextStyleModifier.isItalic(this.modifier)
		set(value) = this.setModifier(TextStyleModifier.ITALIC, value)

	var underlined: Boolean
		get() = TextStyleModifier.isUnderlined(this.modifier)
		set(value) = this.setModifier(TextStyleModifier.UNDERLINED, value)

	var strikethrough: Boolean
		get() = TextStyleModifier.isStrikethrough(this.modifier)
		set(value) = this.setModifier(TextStyleModifier.STRIKETHROUGH, value)

	var obfuscated: Boolean
		get() = TextStyleModifier.isObfuscated(this.modifier)
		set(value) = this.setModifier(TextStyleModifier.OBFUSCATED, value)

	private fun setModifier(@TextStyleModifier.Mask flag: Int, enable: Boolean) {
		if (enable)
			this.modifier = this.modifier or flag
		else
			this.modifier = this.modifier and flag.inv()
	}

	fun build(): TextStyle =
		TextStyle(modifier, color)
}
