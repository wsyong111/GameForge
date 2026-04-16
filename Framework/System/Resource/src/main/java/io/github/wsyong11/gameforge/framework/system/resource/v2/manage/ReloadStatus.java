package io.github.wsyong11.gameforge.framework.system.resource.v2.manage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface ReloadStatus {
	@NotNull
	@UnmodifiableView
	List<Stage> getStages();

	@NotNull
	Stage getCurrentStage();

	boolean isDone();

	boolean isSuccess();

	@Nullable
	Throwable getException();

	void await();

	void await(long timeout, @NotNull TimeUnit unit);

	void addListener(@NotNull Listener listener);

	void removeListener(@NotNull Listener listener);

	interface Stage {
		@NotNull
		String getId();

		@NotNull
		State getState();

		int getTotal();

		int getCurrent();

		default float getProgress() {
			return (float) this.getCurrent() / this.getTotal();
		}
	}

	interface Listener {
		void onStageUpdate(@NotNull Stage stage);

		void onStageProgressUpdate(@NotNull Stage stage);

		void onSuccess();

		void onFailed(@NotNull Throwable exception);
	}

	enum State {
		PENDING,
		RUNNING,
		DONE,
		FAILED
	}
}
