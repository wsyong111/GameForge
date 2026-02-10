package io.github.wsyong11.gameforge.framework.context.dump;

import io.github.wsyong11.gameforge.util.debug.ThreadSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class StackItemDumpInfo {
	private final Type type;
	@Nullable
	private final String contextToString;

	public final boolean declaredDebug;
	public final boolean effectiveDebug;

	@Nullable
	private final ThreadSnapshot pushThreadSnapshot;

	public StackItemDumpInfo(
		@NotNull Type type,
		@Nullable String contextToString,
		boolean declaredDebug,
		boolean effectiveDebug,
		@Nullable ThreadSnapshot pushThreadSnapshot
	) {
		Objects.requireNonNull(type, "type is null");

		this.type = type;
		this.contextToString = contextToString;
		this.declaredDebug = declaredDebug;
		this.effectiveDebug = effectiveDebug;
		this.pushThreadSnapshot = pushThreadSnapshot;
	}

	@NotNull
	public Type getType() {
		return this.type;
	}

	@Nullable
	public String getContextToString() {
		return this.contextToString;
	}

	public boolean isDeclaredDebug() {
		return this.declaredDebug;
	}

	public boolean isEffectiveDebug() {
		return this.effectiveDebug;
	}

	@Nullable
	public ThreadSnapshot getPushThreadSnapshot() {
		return this.pushThreadSnapshot;
	}

	public enum Type {
		CONTEXT,
		HIDDEN,
		DISABLE
	}
}
