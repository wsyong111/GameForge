package io.github.wsyong11.gameforge.framework.system.render.context;

import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniondc;
import org.joml.Quaternionfc;
import org.joml.Vector3dc;

import java.util.function.Consumer;

public interface InstanceBuilder {
	@NotNull
	InstanceBuilder translate(double x, double y, double z);

	@NotNull
	InstanceBuilder translate(@NotNull Vector3dc translate);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	InstanceBuilder scale(float scale);

	@NotNull
	InstanceBuilder scale(float x, float y, float z);

	@NotNull
	InstanceBuilder scale(@NotNull Vector3dc scale);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	InstanceBuilder rotationEuler(float pitch, float yaw, float roll); // 欧拉角

	@NotNull
	InstanceBuilder rotation(@NotNull Quaternionfc q); // 四元数

	@NotNull
	InstanceBuilder rotateAxis(float angleDeg, float ax, float ay, float az);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	InstanceBuilder attribute(@NotNull String name, float... values);

	// -------------------------------------------------------------------------------------------------------------- //

	@NotNull
	InstanceBuilder repeat(int count, @NotNull Consumer<InstanceBuilder> consumer);

	@NotNull
	InstanceBuilder repeatIndexed(int count, @NotNull RepeatConsumer consumer);

	@NotNull
	RenderContext end();

	@FunctionalInterface
	interface RepeatConsumer {
		void apply(@NotNull InstanceBuilder builder, int i);
	}
}
