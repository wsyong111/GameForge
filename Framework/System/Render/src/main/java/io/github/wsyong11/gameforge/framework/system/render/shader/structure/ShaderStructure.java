package io.github.wsyong11.gameforge.framework.system.render.shader.structure;

import io.github.wsyong11.gameforge.framework.system.render.shader.ShaderType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.Set;


/*
ShaderStructure
 ├─ Attributes (name, type, location)
 ├─ Uniforms (name, type, offset, size)
 ├─ UniformBlocks (name, size, list<Uniform>)
 ├─ Samplers (name, type, binding)
 ├─ StorageBuffers (name, size, binding)
 └─ Outputs (name, type, location)
 */
public interface ShaderStructure {
	@NotNull
	@UnmodifiableView
	List<ShaderAttribute> getAttributes();

	@NotNull
	@UnmodifiableView
	List<ShaderUniform> getUniforms();

	@NotNull
	Set<ShaderType> getAvailableShaders();

	@Nullable
	String getSourceCode(@NotNull ShaderType type);
}
