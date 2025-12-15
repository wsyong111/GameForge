package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.config.ex.RuntimeCodecException;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

/**
 * 基准测试 {@link PreferenceStorage} 的序列化和反序列化性能
 */
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1) // 不预热
@Measurement(iterations = 5, time = 2)
@Threads(4)
@State(Scope.Thread)
public class PreferenceStorageBenchmark {
	private PreferenceStorage storage;

	@Param("100")
	private int numEntries;

	@Setup(Level.Iteration)
	public void setup() {
		// 这里假设你有一个实现类 InMemoryPreferenceStorage
		storage = new ElementPreferenceStorage(MutableElement.object(), () -> {
		});
	}

	@Benchmark
	public void testSerialization() throws RuntimeCodecException {
		PreferenceStorage.Editor editor = storage.edit();
		for (int i = 0; i < numEntries; i++) {
			editor.setValue("key_" + i, "value_" + i, String.class);
		}
		editor.apply(); // 提交，触发序列化
	}

	@Benchmark
	public void testDeserialization() throws RuntimeCodecException {
		// 先保证有数据
		PreferenceStorage.Editor editor = storage.edit();
		for (int i = 0; i < numEntries; i++) {
			editor.setValue("key_" + i, "value_" + i, String.class);
		}
		editor.apply();

		// 反序列化测试
		for (int i = 0; i < numEntries; i++) {
			storage.getValue("key_" + i, String.class);
		}
	}
}
