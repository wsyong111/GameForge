package io.github.wsyong11.gameforge.framework;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 表示一个不可变的颜色对象，使用默认的 {@code ARGB} 格式存储。
 * <p>
 * Alpha 值范围为 {@code 0~255}，其中：
 * <ul>
 *   <li>{@code 0} 表示完全透明</li>
 *   <li>{@code 255} 表示完全不透明</li>
 * </ul>
 * <p>
 * 为了减少重复对象的创建，部分颜色会自动缓存，调用 {@link #intern()} 可将当前实例加入缓存。
 * 建议通过 {@link #ofArgb(int)}、{@link #ofRgb(int)} 等工厂方法获取实例，而不是直接使用构造函数。
 *
 * <h3>示例</h3>
 * <pre>{@code
 * Color red = Color.RED;
 * Color custom = Color.ofArgb(128, 255, 200, 100); // 半透明颜色
 * int argb = custom.toArgb(); // 获取 ARGB 整数值
 * Vector4f vec = custom.toNormalizeRgba(); // 转换为归一化的 RGBA 向量 (0.0~1.0)
 * }</pre>
 *
 * @since 1.0
 */
public final class Color {
	// Default ARGB
	// Transparent Alpha = 0

	private static final Map<Integer, Color> caches = new ConcurrentHashMap<>();

	// Grayscale
	//@formatter:off
	public static final Color BLACK      = new Color(0  , 0  , 0  ).intern();
	public static final Color DARK_GRAY  = new Color(64 , 64 , 64 ).intern();
	public static final Color GRAY       = new Color(128, 128, 128).intern();
	public static final Color LIGHT_GRAY = new Color(192, 192, 192).intern();
	public static final Color WHITE      = new Color(255, 255, 255).intern();
	//@formatter:on

	// Primary & Secondary Colors (Hue order)
	//@formatter:off
	public static final Color RED     = new Color(255, 0  , 0  ).intern();
	public static final Color YELLOW  = new Color(255, 255, 0  ).intern();
	public static final Color GREEN   = new Color(0  , 255, 0  ).intern();
	public static final Color CYAN    = new Color(0  , 255, 255).intern();
	public static final Color BLUE    = new Color(0  , 0  , 255).intern();
	public static final Color MAGENTA = new Color(255, 0  , 255).intern();
	//@formatter:on

	/**
	 * 完全透明的颜色，ARGB 值为 {@code 0x00000000}。
	 */
	public static final Color TRANSPARENT = new Color(0x00000000).intern();

	// -------------------------------------------------------------------------------------------------------------- //
	// ARGB

	/**
	 * 根据 {@code ARGB} 整数值创建颜色对象。
	 * 若缓存中存在相同值，将返回缓存实例。
	 *
	 * @param argb ARGB 格式颜色值，范围 {@code 0x00000000 ~ 0xFFFFFFFF}
	 * @return 对应的 {@link Color} 实例
	 */
	@NotNull
	public static Color ofArgb(int argb) {
		Color cachedColor = caches.get(argb);
		if (cachedColor != null)
			return cachedColor;

		return new Color(argb);
	}

	/**
	 * 根据分量值创建 ARGB 颜色。
	 *
	 * @param alpha 透明度，范围 {@code 0~255}
	 * @param red   红色分量，范围 {@code 0~255}
	 * @param green 绿色分量，范围 {@code 0~255}
	 * @param blue  蓝色分量，范围 {@code 0~255}
	 * @return 对应的 {@link Color} 实例
	 */
	@NotNull
	public static Color ofArgb(int alpha, int red, int green, int blue) {
		return ofArgb(toArgb(alpha, red, green, blue));
	}

	public static int toArgb(int alpha, int red, int green, int blue) {
		//@formatter:off
		return ((alpha & 0xFF) << 24)
			 | ((red   & 0xFF) << 16)
			 | ((green & 0xFF) << 8 )
			 | ((blue  & 0xFF)      );
		//@formatter:on
	}

	public static int toArgb(int red, int green, int blue) {
		return toArgb(255, red, green, blue);
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// RGB

	/**
	 * 根据 RGB 数值创建颜色对象，自动补全 Alpha=255（完全不透明）。
	 *
	 * @param rgb RGB 格式颜色值，范围 {@code 0x000000 ~ 0xFFFFFF}
	 * @return 对应的 {@link Color} 实例
	 */
	@NotNull
	public static Color ofRgb(int rgb) {
		return ofArgb(0xFF000000 | rgb);
	}

	@NotNull
	public static Color ofRgb(int red, int green, int blue) {
		return ofArgb(toArgb(red, green, blue));
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取该颜色的 Alpha 分量。
	 *
	 * @return Alpha 分量（0~255）
	 */
	public static int getAlpha(int argb) {
		return (argb >> 24) & 0xFF;
	}

	/**
	 * 获取该颜色的 Red 分量。
	 *
	 * @return Red 分量（0~255）
	 */
	public static int getRed(int argb) {
		return (argb >> 16) & 0xFF;
	}

	/**
	 * 获取该颜色的 Green 分量。
	 *
	 * @return Green 分量（0~255）
	 */
	public static int getGreen(int argb) {
		return (argb >> 8) & 0xFF;
	}

	/**
	 * 获取该颜色的 Blue 分量。
	 *
	 * @return Blue 分量（0~255）
	 */
	public static int getBlue(int argb) {
		return argb & 0xFF;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	// AARRGGBB
	private final int color;

	private Color(int r, int g, int b) {
		this(toArgb(r, g, b));
	}

	private Color(int argb) {
		this.color = argb;
	}

	@NotNull
	public Color intern() {
		return caches.computeIfAbsent(this.color, k -> this);
	}

	public int getAlpha() {
		return getAlpha(this.color);
	}

	public int getRed() {
		return getRed(this.color);
	}

	public int getGreen() {
		return getGreen(this.color);
	}

	public int getBlue() {
		return getBlue(this.color);
	}

	/**
	 * 获取该颜色的 ARGB 整数值。
	 *
	 * @return {@code 0xAARRGGBB} 格式的整数值
	 */
	public int toArgb() {
		return this.color;
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// RGB

	@NotNull
	public Vector3i toVectorRgb() {
		return this.toVectorRgb(new Vector3i());
	}

	@NotNull
	public Vector3i toVectorRgb(@NotNull Vector3i value) {
		Objects.requireNonNull(value, "value is null");
		value.set(this.getRed(), this.getGreen(), this.getBlue());
		return value;
	}

	@NotNull
	public Vector3f toNormalizeRgb() {
		return this.toNormalizeRgb(new Vector3f());
	}

	@NotNull
	public Vector3f toNormalizeRgb(@NotNull Vector3f value) {
		Objects.requireNonNull(value, "value is null");
		value.set(this.getRed() / 255.0F, this.getGreen() / 255.0F, this.getBlue() / 255.0F);
		return value;
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// RGBA

	@NotNull
	public Vector4i toVectorRgba() {
		return this.toVectorRgba(new Vector4i());
	}

	@NotNull
	public Vector4i toVectorRgba(@NotNull Vector4i value) {
		Objects.requireNonNull(value, "value is null");
		value.set(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
		return value;
	}

	@NotNull
	public Vector4f toNormalizeRgba() {
		return this.toNormalizeRgba(new Vector4f());
	}

	@NotNull
	public Vector4f toNormalizeRgba(@NotNull Vector4f value) {
		Objects.requireNonNull(value, "value is null");
		value.set(this.getRed() / 255.0F, this.getGreen() / 255.0F, this.getBlue() / 255.0F, this.getAlpha() / 255.0F);
		return value;
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// ARGB

	@NotNull
	public Vector4i toVectorArgb() {
		return this.toVectorArgb(new Vector4i());
	}

	@NotNull
	public Vector4i toVectorArgb(@NotNull Vector4i value) {
		Objects.requireNonNull(value, "value is null");
		value.set(this.getAlpha(), this.getRed(), this.getGreen(), this.getBlue());
		return value;
	}

	@NotNull
	public Vector4f toNormalizeArgb() {
		return this.toNormalizeArgb(new Vector4f());
	}

	@NotNull
	public Vector4f toNormalizeArgb(@NotNull Vector4f value) {
		Objects.requireNonNull(value, "value is null");
		value.set(this.getAlpha() / 255.0F, this.getRed() / 255.0F, this.getGreen() / 255.0F, this.getBlue() / 255.0F);
		return value;
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Color other = (Color) o;
		return this.color == other.color;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(this.color);
	}

	@Override
	public String toString() {
		return "Color[ARGB %08X]".formatted(this.color);
	}
}
