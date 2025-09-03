package io.github.wsyong11.gameforge.framework.system.security;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class SecurityContext {
	private static final ThreadLocal<SecurityContext> INSTANCE = ThreadLocal.withInitial(SecurityContext::new);

	@NotNull
	public static SecurityContext get(@NotNull Class<?> caller) {
		SecurityContext context = INSTANCE.get();
		context.set(caller);
		return context;
	}

	private final Thread thread;

	private Class<?> caller;
	private ClassLoader classLoader;

	private SecurityContext() {
		this.thread = Thread.currentThread();

		this.caller = null;
		this.classLoader = null;
	}

	private void set(@NotNull Class<?> caller) {
		Objects.requireNonNull(caller, "caller is null");
		this.caller = caller;
		this.classLoader = caller.getClassLoader();
	}

	@NotNull
	public Class<?> getCaller() {
		return this.caller;
	}

	@NotNull
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

	@NotNull
	public Thread getThread() {
		return this.thread;
	}

	@Override
	public String toString() {
		return "SecurityContext[\"" + this.thread.getName() + "\", " + this.caller.getName() + "]";
	}
}