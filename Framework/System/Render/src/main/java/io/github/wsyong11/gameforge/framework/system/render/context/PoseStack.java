package io.github.wsyong11.gameforge.framework.system.render.context;

import org.jetbrains.annotations.NotNull;
import org.joml.*;

public interface PoseStack {
	@NotNull
	PoseStack push();

	@NotNull
	PoseStack pop();

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	PoseStack translate(float x, float y, float z); // 平移

	@NotNull
	PoseStack translate(@NotNull Vector3fc translate);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	PoseStack scale(float scale);

	@NotNull
	PoseStack scale(float x, float y, float z);

	@NotNull
	PoseStack scale(@NotNull Vector3fc scale);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	PoseStack rotateX(float angleRad);

	@NotNull
	PoseStack rotateY(float angleRad);

	@NotNull
	PoseStack rotateZ(float angleRad);

	@NotNull
	PoseStack rotateEuler(float pitch, float yaw, float roll);

	@NotNull
	PoseStack rotate(@NotNull Quaternionfc q);

	@NotNull
	PoseStack rotateAxis(float angleDeg, float ax, float ay, float az);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	PoseStack mul(@NotNull Matrix4fc matrix);

	@NotNull
	PoseStack loadIdentity();

	@NotNull
	Matrix4fc current();
}
