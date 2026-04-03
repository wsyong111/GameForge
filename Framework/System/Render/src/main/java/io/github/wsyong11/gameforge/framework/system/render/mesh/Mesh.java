package io.github.wsyong11.gameforge.framework.system.render.mesh;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.annotation.nio.DirectBuffer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.nio.IntBuffer;
import java.util.Objects;
import java.util.Set;

public interface Mesh extends AutoCloseable {
	String ATTR_POSITION = "pos";
	String ATTR_NORMAL = "normal";
	String ATTR_UV = "uv";

	@NotNull
	Identifier getId();

	@Nullable
	VertexAttribute getAttribute(@NotNull String name);

	default boolean hasAttribute(@NotNull String name) {
		Objects.requireNonNull(name, "name is null");
		return this.getAttributes().contains(name);
	}

	@NotNull
	@UnmodifiableView
	Set<String> getAttributes();

	@NotNull
	@DirectBuffer
	@UnmodifiableView
	IntBuffer getIndexBuffer();

	int getIndexCount();

	int getVertexCount();

	Backed

	@Override
	void close();
}
