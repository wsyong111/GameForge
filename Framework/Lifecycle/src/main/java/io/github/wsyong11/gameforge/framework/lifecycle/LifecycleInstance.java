package io.github.wsyong11.gameforge.framework.lifecycle;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 生命周期实例基类，此类定义了一些生命周期函数用以在生命周期之前切换时调用
 */
public abstract class LifecycleInstance implements LifecycleProvider {
	protected final Lifecycle lifecycle;

	/**
	 * 通过默认的生命周期实现进行实例化
	 */
	protected LifecycleInstance() {
		this(new DefaultLifecycle());
	}

	/**
	 * 通过默认的生命周期实现进行实例化，并自动附加调试名称
	 *
	 * @param debugName 调试名称
	 */
	protected LifecycleInstance(@NotNull String debugName) {
		this(DebugLifecycle.wrap(new DefaultLifecycle(), debugName));
	}

	/**
	 * 通过给定的生命周期实现进行实例化
	 *
	 * @param lifecycle 生命周期实现
	 */
	protected LifecycleInstance(@NotNull Lifecycle lifecycle) {
		Objects.requireNonNull(lifecycle, "lifecycle is null");
		this.lifecycle = lifecycle;
	}

	/**
	 * 进入 {@link LifecycleState#STARTING} 时调用
	 *
	 * @throws Throwable 运行过程中抛出的错误，将导致生命周期状态切换至 {@link LifecycleState#ERROR}
	 */
	protected abstract void onStarting() throws Throwable;

	/**
	 * 进入 {@link LifecycleState#RUNNING} 时调用
	 *
	 * @throws Throwable 运行过程中抛出的错误，将导致生命周期状态切换至 {@link LifecycleState#ERROR}
	 */
	protected abstract void onRunning() throws Throwable;

	/**
	 * 进入 {@link LifecycleState#STOPPING} 时调用
	 *
	 * @throws Throwable 运行过程中抛出的错误，将导致生命周期状态切换至 {@link LifecycleState#ERROR}
	 */
	protected abstract void onStopping() throws Throwable;


	/**
	 * 进入 {@link LifecycleState#STARTING} 时调用
	 */
	protected abstract void onDestroy();

	/**
	 * 进入 {@link LifecycleState#ERROR} 前调用
	 *
	 * @param exception 运行过程中抛出的错误
	 */
	protected abstract void onError(@NotNull Throwable exception);

	@NotNull
	@Override
	public ILifecycle getLifecycle() {
		return this.lifecycle;
	}

	public void start() {
		this.lifecycle.assertState(LifecycleState.CREATED);

		try {
			this.lifecycle.setState(LifecycleState.STARTING);
			this.onStarting();

			this.lifecycle.setState(LifecycleState.RUNNING);
			this.onRunning();
		} catch (Throwable e) {
			this.processError(e);
		}
	}

	private void processError(@NotNull Throwable exception) {
		try {
			this.onError(exception);
		} finally {
			this.lifecycle.setState(LifecycleState.ERROR);
		}
	}

	public void stop() {
		this.lifecycle.assertState(LifecycleState.RUNNING);

		try {
			this.lifecycle.setState(LifecycleState.STOPPING);
			this.onStopping();

			// Destroy may go exception
			this.onDestroy();
			this.lifecycle.setState(LifecycleState.DESTROYED);
		} catch (Throwable e) {
			this.processError(e);
		}
	}
}
