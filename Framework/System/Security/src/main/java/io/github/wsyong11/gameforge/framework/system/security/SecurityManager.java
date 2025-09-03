package io.github.wsyong11.gameforge.framework.system.security;

import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.security.ex.AccessDeniedException;
import io.github.wsyong11.gameforge.framework.system.security.permission.ModifySecurityManagerPermission;
import io.github.wsyong11.gameforge.framework.system.security.permission.Permission;
import io.github.wsyong11.gameforge.util.reflect.ReflectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class SecurityManager {
	private static final Logger LOGGER = Log.getLogger();

	private static final StackWalker STACK_WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
	private static final Set<Class<?>> STACK_WALKER_BLACKLIST = Set.of(
		SecurityManager.class,
		SecurityContext.class
	);

	@Nullable
	private static volatile SecurityManager activeInstance = null;

	public static synchronized void setActiveInstance(@NotNull SecurityManager instance) {
		Objects.requireNonNull(instance, "instance is null");
		checkPermission(ModifySecurityManagerPermission.INSTANCE);

		SecurityManager oldInstance = activeInstance;
		activeInstance = instance;

		LOGGER.info("Active instance changed: {} -> {}", oldInstance, instance);
	}

	public static void checkPermission(@NotNull Permission permission) {
		Objects.requireNonNull(permission, "permission is null");

		SecurityManager instance = activeInstance;
		if (instance == null)
			return;

		Class<?> callerClass = ReflectUtils.getCallerClass(STACK_WALKER, STACK_WALKER_BLACKLIST);
		if (callerClass == null)
			throw new AccessDeniedException("Cannot get caller class");

		SecurityContext context = SecurityContext.get(callerClass);

		if (LOGGER.isTraceEnabled())
			LOGGER.trace("Checking permission {} using context {}", permission.getName(), context);

		instance.checkPermission(permission, context);
	}

	// -------------------------------------------------------------------------------------------------------------- //

	protected void checkPermission(@NotNull Permission permission, @NotNull SecurityContext context) {
		Objects.requireNonNull(permission, "permission is null");

	}
}
