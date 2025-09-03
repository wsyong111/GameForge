package io.github.wsyong11.gameforge.framework.text.rich

import io.github.wsyong11.gameforge.framework.Color
import io.github.wsyong11.gameforge.framework.text.rich.TextStyleModifier

data class TextStyle(
	@TextStyleModifier.Mask
	private val modifier: Int = 0,
	private val color: Color = Color.BLACK,
) {
	fun withColor(color: Color) =
		this.copy(color = color)

	fun withModifierFlag(flag: Int) =
		this.copy(modifier = this.modifier or flag)

	fun withoutModifierFlag(flag: Int) =
		this.copy(modifier = this.modifier and flag.inv())

	fun withBold() =
		this.withModifierFlag(TextStyleModifier.BOLD)

	fun withItalic() =
		this.withModifierFlag(TextStyleModifier.ITALIC)

	fun withUnderlined() =
		this.withModifierFlag(TextStyleModifier.UNDERLINED)

	fun withStrikethrough() =
		this.withModifierFlag(TextStyleModifier.STRIKETHROUGH)

	fun withObfuscated() =
		this.withModifierFlag(TextStyleModifier.OBFUSCATED)

	companion object {
		val EMPTY = TextStyle()
	}
}
