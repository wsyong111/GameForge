package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommand;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommands;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class SimpleRenderCommandPool implements RenderCommandPool {
	private static final int SEGMENT_SHIFT = 5;
	private static final int SEGMENT_SIZE = 1 << SEGMENT_SHIFT;
	private static final int SEGMENT_MASK = SEGMENT_SIZE - 1;

	private final int typeMaxSize;

	private Bucket[][] segments;

	public SimpleRenderCommandPool(int typeMaxSize) {
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

		RenderCommand head = bucket.head;
		if (head == null)
			return null;

		bucket.head = head.getNext();
		bucket.count--;
		head.setNext(null);

		return (T) head;
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

		if (bucket.count >= this.typeMaxSize)
			return false;

		cmd.reset();
		cmd.setNext(bucket.head);
		bucket.head = cmd;
		bucket.count++;
		return true;
	}

	protected static class Bucket {
		public RenderCommand head = null;
		public int count = 0;
	}
}
