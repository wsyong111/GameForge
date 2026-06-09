package io.github.wsyong11.gameforge.framework;

import io.github.wsyong11.gameforge.util.number.Hex;
import io.github.wsyong11.gameforge.util.number.Maths;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.lang.Math;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntUnaryOperator;

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
 * Color custom = Color.of(128, 255, 200, 100); // 半透明颜色
 * int argb = custom.toArgb(); // 获取 ARGB 整数值
 * Vector4f vec = custom.toNormalizeRgba(); // 转换为归一化的 RGBA 向量 (0.0~1.0)
 * }</pre>
 *
 * @since 1.0
 */
public final class Color {
	// Default ARGB
	// Transparent Alpha = 0

	private static final float INV_255 = 1.0f / 255.0f;
	private static final int ALPHA_SHIFT = 24;
	private static final int RED_SHIFT = 16;
	private static final int GREEN_SHIFT = 8;
	private static final int BLUE_SHIFT = 0;

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

	@NotNull
	private static Color create(int argb) {
		return caches.computeIfAbsent(argb, Color::new);
	}

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
		return create(argb);
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
	public static Color of(int alpha, int red, int green, int blue) {
		return ofArgb(toArgb(alpha, red, green, blue));
	}

	/**
	 * 根据分量值创建 ARGB 颜色。
	 *
	 * @param alpha 透明度，范围 {@code 0.0~1.0}
	 * @param red   红色分量，范围 {@code 0.0~1.0}
	 * @param green 绿色分量，范围 {@code 0.0~1.0}
	 * @param blue  蓝色分量，范围 {@code 0.0~1.0}
	 * @return 对应的 {@link Color} 实例
	 */
	@NotNull
	public static Color of(float alpha, float red, float green, float blue) {
		return ofArgb(toArgb(
			(int) (alpha / INV_255),
			(int) (red / INV_255),
			(int) (green / INV_255),
			(int) (blue / INV_255)
		));
	}

	public static int toArgb(int alpha, int red, int green, int blue) {
		//@formatter:off
		return ((alpha & 0xFF) << ALPHA_SHIFT)
			 | ((red   & 0xFF) << RED_SHIFT  )
			 | ((green & 0xFF) << GREEN_SHIFT)
			 | ((blue  & 0xFF) << BLUE_SHIFT );
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
	public static Color of(int red, int green, int blue) {
		return ofArgb(toArgb(red, green, blue));
	}

	@NotNull
	public static Color of(float red, float green, float blue) {
		return ofArgb(toArgb(
			(int) (red / INV_255),
			(int) (green / INV_255),
			(int) (blue / INV_255)
		));
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// Vector

	@NotNull
	public static Color ofArgb(@NotNull Vector4ic vec) {
		return of(vec.x(), vec.y(), vec.z(), vec.w());
	}

	@NotNull
	public static Color ofArgb(@NotNull Vector4fc vec) {
		return of(vec.x(), vec.y(), vec.z(), vec.w());
	}

	@NotNull
	public static Color ofRgba(@NotNull Vector4ic vec) {
		return of(vec.w(), vec.x(), vec.y(), vec.z());
	}

	@NotNull
	public static Color ofRgba(@NotNull Vector4fc vec) {
		return of(vec.w(), vec.x(), vec.y(), vec.z());
	}

	@NotNull
	public static Color ofRgb(@NotNull Vector3ic vec) {
		return of(vec.x(), vec.y(), vec.z());
	}

	@NotNull
	public static Color ofRgb(@NotNull Vector3fc vec) {
		return of(vec.x(), vec.y(), vec.z());
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// HEX

	@SuppressWarnings("DuplicateExpressions")
	@NotNull
	public static Color ofHex(@NotNull String value) {
		Objects.requireNonNull(value, "value is null");

		if (value.isEmpty())
			throw new IllegalArgumentException("Empty color hex string");

		int startIndex = value.charAt(0) == '#' ? 1 : 0;

		int length = value.length() - startIndex;
		try {
			switch (length) {
				// #RGB
				case 3 -> {
					int r = Integer.parseInt(value, startIndex, startIndex + 1, 16);
					int g = Integer.parseInt(value, startIndex + 1, startIndex + 2, 16);
					int b = Integer.parseInt(value, startIndex + 2, startIndex + 3, 16);

					return of(r, g, b);
				}

				// #RGBA
				case 4 -> {
					int r = Integer.parseInt(value, startIndex, startIndex + 1, 16);
					int g = Integer.parseInt(value, startIndex + 1, startIndex + 2, 16);
					int b = Integer.parseInt(value, startIndex + 2, startIndex + 3, 16);
					int a = Integer.parseInt(value, startIndex + 3, startIndex + 4, 16);

					return of(a, r, g, b);
				}

				// #RRGGBB
				case 6 -> {
					int rgb = Integer.parseInt(value, startIndex, startIndex + 6, 16);
					return ofRgb(rgb);
				}

				// #RRGGBBAA
				case 8 -> {
					long v = Long.parseLong(value, startIndex, startIndex + 8, 16);

					int r = (int) ((v >> 24) & 0xFF);
					int g = (int) ((v >> 16) & 0xFF);
					int b = (int) ((v >> 8) & 0xFF);
					int a = (int) (v & 0xFF);

					return of(a, r, g, b);
				}
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Cannot parse hex color " + value, e);
		}

		throw new IllegalArgumentException("Cannot parse hex color " + value);
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// HSV / HSL

	@NotNull
	public static Color ofHsv(float h, float s, float v) {
		float c = v * s;
		float m = v - c;
		return createWithHue(h, c, m);
	}

	@NotNull
	public static Color ofHsl(float h, float s, float l) {
		float c = (1.0F - Math.abs(2.0F * l - 1.0F)) * s;
		float m = l - c * 0.5F;
		return createWithHue(h, c, m);
	}

	@NotNull
	private static Color createWithHue(float h, float c, float m) {
		float x = c * (1.0F - Math.abs((h / 60.0F) % 2.0F - 1.0F));

		float rp, gp, bp;
		if (h < 60.0F) {
			rp = c;
			gp = x;
			bp = 0;
		} else if (h < 120.0F) {
			rp = x;
			gp = c;
			bp = 0;
		} else if (h < 180.0F) {
			rp = 0;
			gp = c;
			bp = x;
		} else if (h < 240.0F) {
			rp = 0;
			gp = x;
			bp = c;
		} else if (h < 300.0F) {
			rp = x;
			gp = 0;
			bp = c;
		} else {
			rp = c;
			gp = 0;
			bp = x;
		}

		return of(
			rp + m,
			gp + m,
			bp + m
		);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/**
	 * 获取该颜色的 Alpha 分量。
	 *
	 * @return Alpha 分量（0~255）
	 */
	public static int getAlpha(int argb) {
		return (argb >> ALPHA_SHIFT) & 0xFF;
	}

	/**
	 * 获取该颜色的 Red 分量。
	 *
	 * @return Red 分量（0~255）
	 */
	public static int getRed(int argb) {
		return (argb >> RED_SHIFT) & 0xFF;
	}

	/**
	 * 获取该颜色的 Green 分量。
	 *
	 * @return Green 分量（0~255）
	 */
	public static int getGreen(int argb) {
		return (argb >> GREEN_SHIFT) & 0xFF;
	}

	/**
	 * 获取该颜色的 Blue 分量。
	 *
	 * @return Blue 分量（0~255）
	 */
	public static int getBlue(int argb) {
		return (argb >> BLUE_SHIFT) & 0xFF;
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

	public float getAlphaNormalize() {
		return getAlpha(this.color) * INV_255;
	}

	@NotNull
	public Color withAlpha(int a) {
		return create(toArgb(a, this.getRed(), this.getGreen(), this.getBlue()));
	}

	@NotNull
	public Color transformAlpha(@NotNull IntUnaryOperator operator) {
		Objects.requireNonNull(operator, "operator is null");
		return this.withAlpha(operator.applyAsInt(this.getAlpha()));
	}

	public int getRed() {
		return getRed(this.color);
	}

	public float getRedNormalize() {
		return getRed(this.color) * INV_255;
	}

	@NotNull
	public Color withRed(int r) {
		return create(toArgb(this.getAlpha(), r, this.getGreen(), this.getBlue()));
	}

	@NotNull
	public Color transformRed(@NotNull IntUnaryOperator operator) {
		Objects.requireNonNull(operator, "operator is null");
		return this.withRed(operator.applyAsInt(this.getRed()));
	}

	public int getGreen() {
		return getGreen(this.color);
	}

	public float getGreenNormalize() {
		return getGreen(this.color) * INV_255;
	}

	@NotNull
	public Color withGreen(int g) {
		return create(toArgb(this.getAlpha(), this.getRed(), g, this.getBlue()));
	}

	@NotNull
	public Color transformGreen(@NotNull IntUnaryOperator operator) {
		Objects.requireNonNull(operator, "operator is null");
		return this.withGreen(operator.applyAsInt(this.getGreen()));
	}

	public int getBlue() {
		return getBlue(this.color);
	}

	public float getBlueNormalize() {
		return getBlue(this.color) * INV_255;
	}

	@NotNull
	public Color withBlue(int b) {
		return create(toArgb(this.getAlpha(), this.getRed(), this.getGreen(), b));
	}

	@NotNull
	public Color transformBlue(@NotNull IntUnaryOperator operator) {
		Objects.requireNonNull(operator, "operator is null");
		return this.withBlue(operator.applyAsInt(this.getBlue()));
	}

	/**
	 * 获取该颜色的 ARGB 整数值。
	 *
	 * @return {@code 0xAARRGGBB} 格式的整数值
	 */
	public int toArgb() {
		return this.color;
	}

	/**
	 * 获取该颜色的 RGB 整数值。
	 *
	 * @return {@code 0xRRGGBB} 格式的整数值
	 */
	public int toRgb() {
		return this.color & 0xFFFFFF;
	}

	/**
	 * 获取该颜色的 RGBA 整数值。
	 *
	 * @return {@code 0xRRGGBBAA} 格式的整数值
	 */
	public int toRgba() {
		return (this.color << 8)
			| ((this.color >> 24) & 0xFF);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public Color blend(@NotNull Color other, float t) {
		Objects.requireNonNull(other, "other is null");

		float clampedT = Maths.clamp(0.0F, t, 1.0F);

		int a1 = this.getAlpha();
		int r1 = this.getRed();
		int g1 = this.getGreen();
		int b1 = this.getBlue();

		int a2 = other.getAlpha();
		int r2 = other.getRed();
		int g2 = other.getGreen();
		int b2 = other.getBlue();

		int a = Math.round(Maths.lerp((float) a1, (float) a2, clampedT));
		int r = Math.round(Maths.lerp((float) r1, (float) r2, clampedT));
		int g = Math.round(Maths.lerp((float) g1, (float) g2, clampedT));
		int b = Math.round(Maths.lerp((float) b1, (float) b2, clampedT));

		return of(a, r, g, b);
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
		value.set(this.getRedNormalize(), this.getGreenNormalize(), this.getBlueNormalize());
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
		value.set(this.getRedNormalize(), this.getGreenNormalize(), this.getBlueNormalize(), this.getAlphaNormalize());
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
		value.set(this.getAlphaNormalize(), this.getRedNormalize(), this.getGreenNormalize(), this.getBlueNormalize());
		return value;
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// HEX

	@NotNull
	public String toHex() {
		return Hex.toHex(this.toRgba(), 8);
	}

	// -------------------------------------------------------------------------------------------------------------- //
	// HSV / HSL

	private static float calculateHue(float max, float r, float g, float b, float delta) {
		float h;

		if (max == r)
			h = (g - b) / delta;
		else if (max == g)
			h = 2.0F + (b - r) / delta;
		else
			h = 4.0F + (r - g) / delta;

		h *= 60.0F;
		if (h < 0.0F)
			return h + 360.0F;

		return h;
	}

	// HSV

	@NotNull
	public Vector3f toHsv(@NotNull Vector3f dest) {
		Objects.requireNonNull(dest, "dest is null");

		float r = this.getRedNormalize();
		float g = this.getGreenNormalize();
		float b = this.getBlueNormalize();

		float max = Maths.max(r, g, b);
		float min = Maths.min(r, g, b);
		float delta = max - min;

		if (delta == 0.0F) {
			dest.set(0.0F, 0.0F, max);
			return dest;
		}

		float h, s, v;
		h = calculateHue(max, r, g, b, delta);
		v = max;
		s = delta / max;

		dest.set(h, s, v);
		return dest;
	}

	@NotNull
	public Vector3f toHsv() {
		return this.toHsv(new Vector3f());
	}

	// HSL

	@NotNull
	public Vector3f toHsl(@NotNull Vector3f dest) {
		Objects.requireNonNull(dest, "dest is null");

		float r = this.getRedNormalize();
		float g = this.getGreenNormalize();
		float b = this.getBlueNormalize();

		float max = Maths.max(r, g, b);
		float min = Maths.min(r, g, b);
		float delta = max - min;

		if (delta == 0.0F) {
			dest.set(0.0F, 0.0F, max);
			return dest;
		}

		float h, s, l;
		h = calculateHue(max, r, g, b, delta);
		l = (max + min) * 0.5F;
		s = delta / (1.0F - Math.abs(2.0F * l - 1.0F));

		dest.set(h, s, l);
		return dest;
	}

	@NotNull
	public Vector3f toHsl() {
		return this.toHsl(new Vector3f());
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
		return "Color(R=%3d, G=%3d, B=%3d, A=%6.02f%%)".formatted(
			this.getRed(),
			this.getGreen(),
			this.getBlue(),
			this.getAlpha() * INV_255 * 100.0F
		);
	}
}
