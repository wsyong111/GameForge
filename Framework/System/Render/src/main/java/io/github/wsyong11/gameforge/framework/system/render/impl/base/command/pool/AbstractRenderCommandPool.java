package io.github.wsyong11.gameforge.framework.system.render.impl.base.command.pool;

import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommand;
import io.github.wsyong11.gameforge.framework.system.render.impl.base.command.RenderCommands;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractRenderCommandPool<B> implements RenderCommandPool {

    protected static final int SEGMENT_SHIFT = 5;
    protected static final int SEGMENT_SIZE = 1 << SEGMENT_SHIFT;
    protected static final int SEGMENT_MASK = SEGMENT_SIZE - 1;

    protected final int typeMaxSize;
    protected Object[][] segments;

    protected AbstractRenderCommandPool(int typeMaxSize) {
        this.typeMaxSize = typeMaxSize;
        this.segments = new Object[4][];
    }

    @SuppressWarnings("unchecked")
    protected B[] ensureSegment(int segment) {
        if (segment >= this.segments.length) {
            int newSize = this.segments.length;
            while (newSize <= segment)
                newSize <<= 1;

            this.segments = Arrays.copyOf(this.segments, newSize);
        }

        Object[] buckets = this.segments[segment];
        if (buckets == null) {
            buckets = new Object[SEGMENT_SIZE];
            this.segments[segment] = buckets;
        }

        return (B[]) buckets;
    }

    protected B getOrCreateBucket(int id) {
        B[] buckets = ensureSegment(id >>> SEGMENT_SHIFT);
        int idx = id & SEGMENT_MASK;

        B bucket = buckets[idx];
        if (bucket == null) {
            bucket = newBucket();
            buckets[idx] = bucket;
        }
        return bucket;
    }

    protected B getBucket(int id) {
        B[] buckets = ensureSegment(id >>> SEGMENT_SHIFT);
        return buckets[id & SEGMENT_MASK];
    }

    // ===== 模板方法 =====

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RenderCommand> T tryAlloc(Class<T> type) {
        Objects.requireNonNull(type);

        int id = RenderCommands.getId(type);
        B bucket = getBucket(id);
        if (bucket == null)
            return null;

        return (T) pop(bucket);
    }

    @Override
    public boolean tryFree(RenderCommand cmd) {
        Objects.requireNonNull(cmd);

        int id = cmd.getTypeId();
        B bucket = getOrCreateBucket(id);

        return push(bucket, cmd);
    }

    // ===== 抽象点（子类实现）=====

    protected abstract B newBucket();

    protected abstract RenderCommand pop(B bucket);

    protected abstract boolean push(B bucket, RenderCommand cmd);
}
