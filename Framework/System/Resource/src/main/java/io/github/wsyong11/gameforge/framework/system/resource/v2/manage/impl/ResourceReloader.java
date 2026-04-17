package io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl;

import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ReloadStatus;
import io.github.wsyong11.gameforge.util.IdentityRef;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public abstract class ResourceReloader {
	private final List<Stage> stages;
	private volatile int currentStage;

	private final List<IdentityRef<ReloadStatus.Listener>> listeners;

	private final ReloadStatus reloadStatus;

	protected ResourceReloader(@NotNull List<String> stageIds) {
		Objects.requireNonNull(stageIds, "stageIds is null");

		this.stages = stageIds
			.stream()
			.map(Stage::new)
			.toList();

		this.currentStage = 0;

		this.listeners = new CopyOnWriteArrayList<>();

		this.reloadStatus = new ReloadStatusImpl();
	}

	@NotNull
	public String getCurrentStage() {
		return this.stages.get(this.currentStage).getId();
	}

	@NotNull
	public ReloadStatus getStatus() {
		return this.reloadStatus;
		Math.
	}

	private static class Stage implements ReloadStatus.Stage {
		private final String id;

		private volatile ReloadStatus.State state;

		private int total;
		private int current;

		public Stage(@NotNull String id) {
			Objects.requireNonNull(id, "id is null");

			this.id = id;

			this.state = ReloadStatus.State.PENDING;

			this.total = 0;
			this.current = 0;
		}

		public void ensureState(@NotNull ReloadStatus.State state) {
			Objects.requireNonNull(state, "state is null");

			if (this.state != state)
				throw new IllegalStateException("State is not " + state + ", Current state is " + this.state);
		}

		public void ensureState(@NotNull ReloadStatus.State... state) {
			Objects.requireNonNull(state, "state is null");

			if (!ArrayUtils.contains(state, this.state))
				throw new IllegalStateException("State is not " + Arrays.toString(state) + ", Current state is " + this.state);
		}

		public void setTotal(int total) {
			if (total < 0)
				throw new IllegalStateException("Total cannot set to negative");
			this.ensureState(ReloadStatus.State.PENDING);
			this.total = total;
		}

		public void setCurrent(int current) {
			if (current < 0)
				throw new IllegalStateException("Current cannot set to negative");
			this.current = current;
		}

		public void increaseCurrent(int delta) {
			this.current = Math.max(0, Math.min(this.current + delta, this.total));
		}

		public void increaseCurrent() {
			this.increaseCurrent(1);
		}

		public void beginRun() {
			this.ensureState(ReloadStatus.State.PENDING);
			this.state = ReloadStatus.State.RUNNING;
		}

		public void success() {
			this.ensureState(ReloadStatus.State.RUNNING);
			this.state = ReloadStatus.State.DONE;
		}

		public void fail() {
			this.ensureState(ReloadStatus.State.RUNNING);
			this.state = ReloadStatus.State.FAILED;
		}

		@NotNull
		@Override
		public String getId() {
			return this.id;
		}

		@NotNull
		@Override
		public ReloadStatus.State getState() {
			return this.state;
		}

		@Override
		public int getTotal() {
			return this.total;
		}

		@Override
		public int getCurrent() {
			return this.current;
		}
	}

	private class ReloadStatusImpl implements ReloadStatus {
		@NotNull
		@UnmodifiableView
		@Override
		public List<Stage> getStages() {
			return List.copyOf(stages);
		}

		@NotNull
		@Override
		public Stage getCurrentStage() {
			return stages.get(currentStage);
		}

		@Override
		public boolean isDone() {
			return false;
		}

		@Override
		public boolean isSuccess() {
			return false;
		}

		@Nullable
		@Override
		public Throwable getException() {
			return null;
		}

		@Override
		public void await() {

		}

		@Override
		public void await(long timeout, @NotNull TimeUnit unit) {

		}

		@Override
		public void addListener(@NotNull Listener listener) {
			Objects.requireNonNull(listener, "listener is null");
			listeners.add(IdentityRef.of(listener));
		}

		@Override
		public void removeListener(@NotNull Listener listener) {
			Objects.requireNonNull(listener, "listener is null");
			listeners.remove(IdentityRef.of(listener));
		}
	}

	private static class ReloadStatusImpl2 implements ReloadStatus {
		private final Map<String, StageImpl> stageMap
	}
}
