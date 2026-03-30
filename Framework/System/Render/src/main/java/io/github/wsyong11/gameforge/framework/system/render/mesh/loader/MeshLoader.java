package io.github.wsyong11.gameforge.framework.system.render.mesh.loader;

import io.github.wsyong11.gameforge.framework.system.render.mesh.Mesh;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public interface MeshLoader {
	@NotNull
	Mesh load(@NotNull InputStream stream) throws IOException;
}
