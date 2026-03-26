package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommand;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CASRenderCommandPool implements RenderCommandPool {
	private static final int SEGMENT_SHIFT = 5;
	private static final int SEGMENT_SIZE = 1 << SEGMENT_SHIFT;
	private static final int SEGMENT_MASK = SEGMENT_SIZE - 1;

	private final int typeMaxSize;

	private Bucket[][] segments;

	public CASRenderCommandPool(int typeMaxSize) {
		this.typeMaxSize = typeMaxSize;

		this.segments = new Bucket[4][];
	}

	@NotNull
	private Bucket[] ensureSegment(int segment) {
		if (segment >= this.segments.length) {
			int newSize = this.segments.length;
			while (newSize <= segment)
				newSize <<= 1;

			this.segments = Arrays.copyOf(this.segments, newSize);
		}

		Bucket[] buckets = this.segments[segment];
		if (buckets == null) {
			buckets = new Bucket[SEGMENT_SIZE];
			this.segments[segment] = buckets;
		}

		return buckets;
	}

	@NotNull
	protected Bucket[] getBuckets(int id) {
		int segment = id >>> SEGMENT_SHIFT;
		return this.ensureSegment(segment);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Nullable
	public <T extends RenderCommand> T tryAlloc(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		int id = RenderCommands.getId(type);
		Bucket[] buckets = this.getBuckets(id);

		int idx = id & SEGMENT_MASK;
		Bucket bucket = buckets[idx];
		if (bucket == null)
			return null;

		while (true) {
			RenderCommand head = bucket.head.get();
			if (head == null)
				return null;

			RenderCommand next = head.getNext();

			if (bucket.head.compareAndSet(head, next)) {
				bucket.count.decrementAndGet();
				head.setNext(null);
				return (T) head;
			}

			Thread.onSpinWait();
		}
	}

	@Override
	public boolean tryFree(@NotNull RenderCommand cmd) {
		Objects.requireNonNull(cmd, "cmd is null");

		int id = cmd.getTypeId();
		Bucket[] buckets = this.getBuckets(id);

		int idx = id & SEGMENT_MASK;
		Bucket bucket = buckets[idx];
		if (bucket == null)
			buckets[idx] = bucket = new Bucket();

		while (true) {
			int current = bucket.count.get();
			if (current >= this.typeMaxSize)
				return false;

			RenderCommand head = bucket.head.get();

			cmd.reset();
			cmd.setNext(head);

			// CAS 插入
			if (bucket.head.compareAndSet(head, cmd)) {
				bucket.count.incrementAndGet();
				return true;
			}

			Thread.onSpinWait();
		}
	}

	protected static class Bucket {
		public final AtomicReference<RenderCommand> head = new AtomicReference<>(null);
		public final AtomicInteger count = new AtomicInteger(0);
	}
}
