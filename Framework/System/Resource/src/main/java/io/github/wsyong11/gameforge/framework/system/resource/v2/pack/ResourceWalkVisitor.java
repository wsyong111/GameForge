package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface ResourceWalkVisitor {
	@NotNull
	default VisitResult preVisitDirectory(@NotNull ResourcePath path) throws IOException {
		return VisitResult.CONTINUE;
	}

	@NotNull
	VisitResult visitFile(@NotNull ResourcePath path) throws IOException;

	@NotNull
	default VisitResult postVisitDirectory(@NotNull ResourcePath path, @Nullable IOException exception) throws IOException {
		return VisitResult.CONTINUE;
	}

	@NotNull
	default VisitResult visitFailed(@NotNull ResourcePath path, @NotNull IOException exception) {
		return VisitResult.TERMINATE;
	}

	enum VisitResult {
		CONTINUE,
		SKIP_SUBTREE,
		TERMINATE
	}
}
