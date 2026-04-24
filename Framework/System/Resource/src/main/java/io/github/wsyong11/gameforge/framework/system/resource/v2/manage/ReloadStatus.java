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

	@Nullable
	Stage getStage(@NotNull String id);

	boolean isDone();

	boolean isSuccess();

	boolean isCancelled();

	@Nullable
	Throwable getException();

	default float getProgress() {
		List<Stage> stages = this.getStages();

		float total = 0;
		float sum = 0;

		for (Stage s : stages) {
			float weight = s.getWeight();
			total += weight;
			sum += weight * s.getProgress();
		}

		return total == 0 ? 1f : sum / total;
	}

	void await() throws InterruptedException;

	boolean await(long timeout, @NotNull TimeUnit unit) throws InterruptedException;

	void cancel();

	void addListener(@NotNull Listener listener);

	void removeListener(@NotNull Listener listener);

	interface Stage {
		@NotNull
		String getId();

		float getWeight();

		@NotNull
		State getState();

		int getTotal();

		int getCurrent();

		default float getProgress() {
			float current = this.getCurrent();
			float total = this.getTotal();
			return total <= 0.0F ? 0.0F : current / total;
		}
	}

	@FunctionalInterface
	interface Listener {
		void onUpdate(@NotNull ReloadStatus status);
	}

	enum State {
		PENDING,
		RUNNING,
		DONE,
		FAILED
	}
}
