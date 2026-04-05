package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ex.ResourceException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PackWalkVisitor {
	@NotNull
	default VisitResult preVisitDirectory(@NotNull ResourcePath path) throws ResourceException {
		return VisitResult.CONTINUE;
	}

	@NotNull
	VisitResult visitFile(@NotNull ResourcePath path) throws ResourceException;

	@NotNull
	default VisitResult postVisitDirectory(@NotNull ResourcePath path, @Nullable ResourceException exception) throws ResourceException {
		return VisitResult.CONTINUE;
	}

	@NotNull
	default VisitResult visitFailed(@NotNull ResourcePath path, @NotNull ResourceException exception) {
		return VisitResult.TERMINATE;
	}

	enum VisitResult {
		CONTINUE,
		SKIP_SUBTREE,
		TERMINATE
	}
}
