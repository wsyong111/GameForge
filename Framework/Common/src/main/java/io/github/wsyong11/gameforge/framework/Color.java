package io.github.wsyong11.gameforge.framework;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import org.joml.Vector4i;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Color {
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

	public static final Color TRANSPARENT = new Color(0x00000000).intern();

	@NotNull
	public static Color ofArgb(int argb) {
		Color cachedColor = caches.get(argb);
		if (cachedColor != null)
			return cachedColor;

		return new Color(argb);
	}

	@NotNull
	public static Color ofArgb(int alpha, int red, int green, int blue) {
		return ofArgb(toArgb(alpha, red, green, blue));
	}

	@NotNull
	public static Color ofRgb(int rgb) {
		return ofArgb(0xFF000000 | rgb);
	}

	@NotNull
	public static Color ofRgb(int red, int green, int blue) {
		return ofArgb(toArgb(red, green, blue));
	}

	// -------------------------------------------------------------------------------------------------------------- //

	public static int getAlpha(int argb) {
		return (argb >> 24) & 0xFF;
	}

	public static int getRed(int argb) {
		return (argb >> 16) & 0xFF;
	}

	public static int getGreen(int argb) {
		return (argb >> 8) & 0xFF;
	}

	public static int getBlue(int argb) {
		return argb & 0xFF;
	}

	// -------------------------------------------------------------------------------------------------------------- //

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

	// AARRGGBB
	private final int color;

	protected Color(int r, int g, int b) {
		this(toArgb(r, g, b));
	}

	protected Color(int argb) {
		this.color = argb;
	}

	@NotNull
	public Color intern() {
		return caches.computeIfAbsent(this.color, k -> this);
	}

	// -------------------------------------------------------------------------------------------------------------- //

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

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	public Vector3i toVector3Rgb() {
		return toVector3Rgb(new Vector3i());
	}

	@NotNull
	public Vector3i toVector3Rgb(@NotNull Vector3i value) {
		Objects.requireNonNull(value, "value is null");
		value.set(this.getRed(), this.getGreen(), this.getBlue());
		return value;
	}

	@NotNull
	public Vector4i toVector4Rgba() {
		return toVector4Rgba(new Vector4i());
	}

	@NotNull
	public Vector4i toVector4Rgba(@NotNull Vector4i value) {
		Objects.requireNonNull(value, "value is null");
		value.set(this.getRed(), this.getGreen(), this.getBlue(), this.getAlpha());
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
