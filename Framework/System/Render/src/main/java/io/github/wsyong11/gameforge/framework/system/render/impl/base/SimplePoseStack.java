package io.github.wsyong11.gameforge.framework.system.render.impl.base;

import io.github.wsyong11.gameforge.framework.system.render.context.PoseStack;
import org.jetbrains.annotations.NotNull;
import org.joml.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Objects;

public class SimplePoseStack implements PoseStack {
	private final Deque<Matrix4f> stack;

	private Matrix4f current;

	public SimplePoseStack() {
		this.stack = new LinkedList<>();

		this.current = null;

		this.push();
	}

	public void reset() {
		this.stack.clear();
		this.current = null;

		this.push();
	}

	@NotNull
	@Override
	public PoseStack push() {
		Matrix4f matrix = new Matrix4f();
		if (this.current != null)
			matrix.set(this.current);

		this.stack.push(matrix);
		this.current = matrix;

		return this;
	}

	@NotNull
	@Override
	public PoseStack pop() {
		if (this.stack.size() == 1)
			throw new IllegalStateException("Top level pose cannot pop");

		this.stack.pop();
		this.current = this.stack.peek();

		return this;
	}

	@NotNull
	@Override
	public PoseStack translate(float x, float y, float z) {
		this.current.translate(x, y, z);
		return this;
	}

	@NotNull
	@Override
	public PoseStack translate(@NotNull Vector3fc translate) {
		Objects.requireNonNull(translate, "translate is null");
		this.current.translate(translate);
		return this;
	}

	@NotNull
	@Override
	public PoseStack scale(float scale) {
		this.current.scale(scale, scale, scale);
		return this;
	}

	@NotNull
	@Override
	public PoseStack scale(float x, float y, float z) {
		this.current.scale(x, y, z);
		return this;
	}

	@NotNull
	@Override
	public PoseStack scale(@NotNull Vector3fc scale) {
		Objects.requireNonNull(scale, "scale is null");
		this.current.scale(scale);
		return this;
	}

	@NotNull
	@Override
	public PoseStack rotateX(float angleRad) {
		this.current.rotateX(angleRad);
		return this;
	}

	@NotNull
	@Override
	public PoseStack rotateY(float angleRad) {
		this.current.rotateY(angleRad);
		return this;
	}

	@NotNull
	@Override
	public PoseStack rotateZ(float angleRad) {
		this.current.rotateZ(angleRad);
		return this;
	}

	@NotNull
	@Override
	public PoseStack rotateEuler(float pitch, float yaw, float roll) {
		return this.rotate(new Quaternionf().rotateXYZ(pitch, yaw, roll));
	}

	@NotNull
	@Override
	public PoseStack rotate(@NotNull Quaternionfc q) {
		Objects.requireNonNull(q, "q is null");
		this.current.rotate(q);
		return this;
	}

	@NotNull
	@Override
	public PoseStack rotateAxis(float angleDeg, float ax, float ay, float az) {
		this.current.rotate(angleDeg, ax, ay, az);
		return this;
	}

	@NotNull
	@Override
	public PoseStack mul(@NotNull Matrix4fc matrix) {
		Objects.requireNonNull(matrix, "matrix is null");
		this.current.mul(matrix);
		return this;
	}

	@NotNull
	@Override
	public PoseStack loadIdentity() {
		this.current.identity();
		return this;
	}

	@NotNull
	@Override
	public Matrix4fc current() {
		return this.current;
	}
}
