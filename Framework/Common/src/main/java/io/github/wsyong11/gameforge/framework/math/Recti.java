package io.github.wsyong11.gameforge.framework.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joml.Options;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.text.NumberFormat;
import java.util.Objects;

public class Recti implements Rectic {
	@NotNull
	public static Recti ofPosSize(int x, int y, int width, int height) {
		return new Recti(x, y, x + width, y + height);
	}

	@NotNull
	public static Recti ofPosSize(@NotNull Vector2ic pos, @NotNull Vector2ic size) {
		return new Recti(
			pos.x(),
			pos.y(),
			pos.x() + size.x(),
			pos.y() + size.y()
		);
	}

	@NotNull
	public static Recti ofCenter(int centerX, int centerY, int width, int height) {
		int halfW = width / 2;
		int halfH = height / 2;

		return new Recti(
			centerX - halfW,
			centerY - halfH,
			centerX + halfW,
			centerY + halfH
		);
	}

	@NotNull
	public static Recti ofCenter(@NotNull Vector2ic center, @NotNull Vector2ic size) {
		int halfW = size.x() / 2;
		int halfH = size.y() / 2;

		return new Recti(
			center.x() - halfW,
			center.y() - halfH,
			center.x() + halfW,
			center.y() + halfH
		);
	}

	@NotNull
	public static Recti ofSize(int width, int height) {
		return new Recti(0, 0, width, height);
	}

	@NotNull
	public static Recti ofSize(@NotNull Vector2ic size) {
		return new Recti(0, 0, size.x(), size.y());
	}

	public int minX;
	public int minY;
	public int maxX;
	public int maxY;

	public Recti() {
		this.minX = 0;
		this.minY = 0;
		this.maxX = 0;
		this.maxY = 0;
	}

	public Recti(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public Recti(@NotNull Vector2ic min, @NotNull Vector2ic max) {
		this.minX = min.x();
		this.minY = min.y();
		this.maxX = max.x();
		this.maxY = max.y();
	}

	public Recti(@NotNull Rectic rect) {
		this.minX = rect.minX();
		this.minY = rect.minY();
		this.maxX = rect.maxX();
		this.maxY = rect.maxY();
	}

	public Recti(@NotNull Recti rect) {
		this.minX = rect.minX;
		this.minY = rect.minY;
		this.maxX = rect.maxX;
		this.maxY = rect.maxY;
	}

	@Override
	public int minX() {
		return this.minX;
	}

	@Override
	public int minY() {
		return this.minY;
	}

	@Override
	public int maxX() {
		return this.maxX;
	}

	@Override
	public int maxY() {
		return this.maxY;
	}

	@Override
	public int width() {
		return this.maxX - this.minX;
	}

	@Override
	public int height() {
		return this.maxY - this.minY;
	}

	@Override
	public int centerX() {
		return (this.minX + this.maxX) / 2;
	}

	@Override
	public int centerY() {
		return (this.minY + this.maxY) / 2;
	}

	@Override
	public boolean isEmpty() {
		return this.maxX <= this.minX
			|| this.maxY <= this.minY;
	}

	@Override
	public int left() {
		return this.minX;
	}

	@Override
	public int right() {
		return this.maxX;
	}

	@Override
	public int top() {
		return this.minY;
	}

	@Override
	public int bottom() {
		return this.maxY;
	}

	@Override
	@Contract("_ -> param1")
	@NotNull
	public Vector2i min(@NotNull Vector2i dest) {
		dest.x = this.minX;
		dest.y = this.minY;
		return dest;
	}

	@Override
	@Contract("_ -> param1")
	@NotNull
	public Vector2i max(@NotNull Vector2i dest) {
		dest.x = this.maxX;
		dest.y = this.maxY;
		return dest;
	}

	@NotNull
	public Recti set(int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		return this;
	}

	@NotNull
	public Recti set(@NotNull Vector2ic min, @NotNull Vector2ic max) {
		return this.set(min.x(), min.y(), max.x(), max.y());
	}

	@NotNull
	public Recti set(@NotNull Recti r) {
		this.minX = r.minX;
		this.minY = r.minY;
		this.maxX = r.maxX;
		this.maxY = r.maxY;
		return this;
	}

	@NotNull
	public Recti setMin(int x, int y) {
		this.minX = x;
		this.minY = y;
		return this;
	}

	@NotNull
	public Recti setMin(@NotNull Vector2ic min) {
		return this.setMin(min.x(), this.minY = min.y());
	}

	@NotNull
	public Recti setMax(int x, int y) {
		this.maxX = x;
		this.maxY = y;
		return this;
	}

	@NotNull
	public Recti setMax(@NotNull Vector2ic max) {
		return this.setMax(max.x(), max.y());
	}

	@NotNull
	public Recti expand(int dx, int dy) {
		this.minX -= dx;
		this.minY -= dy;
		this.maxX += dx;
		this.maxY += dy;
		return this;
	}

	@NotNull
	public Recti expand(int amount) {
		return this.expand(amount, amount);
	}

	@NotNull
	public Recti expand(@NotNull Vector2ic delta) {
		return this.expand(delta.x(), delta.y());
	}

	@NotNull
	public Recti shrink(int dx, int dy) {
		this.minX += dx;
		this.minY += dy;
		this.maxX -= dx;
		this.maxY -= dy;
		return this;
	}

	@NotNull
	public Recti shrink(int amount) {
		return this.shrink(amount, amount);
	}

	@NotNull
	public Recti shrink(@NotNull Vector2ic delta) {
		return this.shrink(delta.x(), delta.y());
	}

	@NotNull
	public Recti grow(int left, int top, int right, int bottom) {
		this.minX -= left;
		this.minY -= top;
		this.maxX += right;
		this.maxY += bottom;
		return this;
	}

	@NotNull
	public Recti translate(int dx, int dy) {
		this.minX += dx;
		this.minY += dy;
		this.maxX += dx;
		this.maxY += dy;
		return this;
	}

	@NotNull
	public Recti translate(@NotNull Vector2ic delta) {
		return this.translate(delta.x(), delta.y());
	}

	@NotNull
	public Recti intersect(@NotNull Rectic o) {
		this.minX = Math.max(Math.min(this.minX, this.maxX), Math.min(o.minX(), o.maxX()));
		this.minY = Math.max(Math.min(this.minY, this.maxY), Math.min(o.minY(), o.maxY()));

		this.maxX = Math.min(Math.max(this.minX, this.maxX), Math.max(o.minX(), o.maxX()));
		this.maxY = Math.min(Math.max(this.minY, this.maxY), Math.max(o.minY(), o.maxY()));

		return this;
	}

	public boolean intersects(@NotNull Rectic o) {
		int aMinX = Math.min(this.minX, this.maxX);
		int aMaxX = Math.max(this.minX, this.maxX);
		int aMinY = Math.min(this.minY, this.maxY);
		int aMaxY = Math.max(this.minY, this.maxY);

		int bMinX = Math.min(o.minX(), o.maxX());
		int bMaxX = Math.max(o.minX(), o.maxX());
		int bMinY = Math.min(o.minY(), o.maxY());
		int bMaxY = Math.max(o.minY(), o.maxY());

		return aMinX < bMaxX && aMaxX > bMinX
			&& aMinY < bMaxY && aMaxY > bMinY;
	}

	@NotNull
	public Recti union(@NotNull Rectic o) {
		int aMinX = Math.min(this.minX, this.maxX);
		int aMaxX = Math.max(this.minX, this.maxX);
		int aMinY = Math.min(this.minY, this.maxY);
		int aMaxY = Math.max(this.minY, this.maxY);

		int bMinX = Math.min(o.minX(), o.maxX());
		int bMaxX = Math.max(o.minX(), o.maxX());
		int bMinY = Math.min(o.minY(), o.maxY());
		int bMaxY = Math.max(o.minY(), o.maxY());

		this.minX = Math.min(aMinX, bMinX);
		this.minY = Math.min(aMinY, bMinY);
		this.maxX = Math.max(aMaxX, bMaxX);
		this.maxY = Math.max(aMaxY, bMaxY);

		return this;
	}

	@Override
	public boolean contains(int x, int y) {
		return x >= Math.min(this.minX, this.maxX)
			&& x <= Math.max(this.minX, this.maxX)
			&& y >= Math.min(this.minY, this.maxY)
			&& y <= Math.max(this.minY, this.maxY);
	}

	@Override
	public boolean contains(@NotNull Vector2ic pos) {
		return this.contains(pos.x(), pos.y());
	}

	@Override
	public boolean contains(@NotNull Recti other) {
		return this.contains(other.minX(), other.minY())
			&& this.contains(other.maxX(), other.maxY());
	}

	@NotNull
	public Recti clamp(@NotNull Rectic b) {
		int bMinX = Math.min(b.minX(), b.maxX());
		int bMaxX = Math.max(b.minX(), b.maxX());
		int bMinY = Math.min(b.minY(), b.maxY());
		int bMaxY = Math.max(b.minY(), b.maxY());

		this.minX = Math.max(this.minX, bMinX);
		this.minY = Math.max(this.minY, bMinY);
		this.maxX = Math.min(this.maxX, bMaxX);
		this.maxY = Math.min(this.maxY, bMaxY);

		return this;
	}

	@Override
	public int distanceTo(int x, int y) {
		int rxMin = Math.min(this.minX, this.maxX);
		int rxMax = Math.max(this.minX, this.maxX);
		int ryMin = Math.min(this.minY, this.maxY);
		int ryMax = Math.max(this.minY, this.maxY);

		int dx = 0;
		if (x < rxMin) dx = rxMin - x;
		else if (x > rxMax) dx = x - rxMax;

		int dy = 0;
		if (y < ryMin) dy = ryMin - y;
		else if (y > ryMax) dy = y - ryMax;

		return dx + dy;
	}

	@Override
	public int distanceTo(@NotNull Vector2ic pos) {
		return this.distanceTo(pos.x(), pos.y());
	}

	@Override
	public int area() {
		int w = Math.abs(this.maxX - this.minX);
		int h = Math.abs(this.maxY - this.minY);
		return w * h;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Recti rect = (Recti) o;
		return this.minX == rect.minX
			&& this.minY == rect.minY
			&& this.maxX == rect.maxX
			&& this.maxY == rect.maxY;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.minX, this.minY, this.maxX, this.maxY);
	}

	@Override
	public String toString() {
		return this.toString(Options.NUMBER_FORMAT);
	}

	@NotNull
	public String toString(@NotNull NumberFormat formatter) {
		return "("
			+ formatter.format(this.minX) + " " + formatter.format(this.minY)
			+ " ~ "
			+ formatter.format(this.maxX) + " " + formatter.format(this.maxY)
			+ ")";
	}
}
