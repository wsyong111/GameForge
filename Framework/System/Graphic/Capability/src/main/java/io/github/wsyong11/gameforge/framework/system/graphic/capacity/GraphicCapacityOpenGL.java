package io.github.wsyong11.gameforge.framework.system.graphic.capacity;

import java.util.Objects;

public class GraphicCapacityOpenGL {
	private final int major;
	private final int minor;

	public GraphicCapacityOpenGL(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}

	public int getMajor() {
		return this.major;
	}

	public int getMinor() {
		return this.minor;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GraphicCapacityOpenGL that = (GraphicCapacityOpenGL) o;
		return this.major == that.major
			&& this.minor == that.minor;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.major, this.minor);
	}

	@Override
	public String toString() {
		return "OpenGLCapacity[" + this.major + "." + this.minor + "]";
	}
}

