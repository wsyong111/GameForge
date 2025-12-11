package io.github.wsyong11.gameforge.framework.config.preference;

import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableElement;
import io.github.wsyong11.gameforge.framework.dataflow.element.mutable.MutableObjectElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

public class ElementPreferenceStorageTest {
	private MutableObjectElement root;
	private AtomicBoolean modifyCalled;
	private ElementPreferenceStorage storage;

	@BeforeEach
	void setup() {
		root = MutableElement.object();
		modifyCalled = new AtomicBoolean(false);
		storage = new ElementPreferenceStorage(root, () -> modifyCalled.set(true));
	}

	@Test
	void testSetAndGetValue() {
		storage.edit()
		       .setValue("player.level", 10, Integer.class)
		       .apply();

		assertEquals(10, storage.getValue("player.level", Integer.class));
		assertTrue(storage.contains("player.level"));
		assertTrue(modifyCalled.get());
	}

	@Test
	void testNestedPathCreation() {
		storage.edit()
		       .setValue("graphics.quality.shadow", "High", String.class)
		       .apply();

		assertEquals("High", storage.getValue("graphics.quality.shadow", String.class));

		// 检查 element 树结构是否正确
		MutableObjectElement graphics = (MutableObjectElement) root.get("graphics");
		assertNotNull(graphics);

		MutableObjectElement quality = (MutableObjectElement) graphics.get("quality");
		assertNotNull(quality);

		MutableObjectElement shadowNode = (MutableObjectElement) quality.get("shadow");
		assertNotNull(shadowNode);

		assertNotNull(shadowNode.get("@value"));
	}

	@Test
	void testDeleteKey() {
		storage.edit()
		       .setValue("user.name", "Alice", String.class)
		       .apply();

		storage.edit()
		       .delete("user.name")
		       .apply();

		assertFalse(storage.contains("user.name"));
		assertNull(storage.getValue("user.name", String.class));

		// 节点本身还存在（不会自动清理）
		MutableObjectElement user = (MutableObjectElement) root.get("user");
		assertNotNull(user);
	}

	@Test
	void testClearAll() {
		storage.edit()
		       .setValue("a.b", 123, Integer.class)
		       .setValue("x.y", "Hello", String.class)
		       .apply();

		storage.edit()
		       .clearAll()
		       .apply();

		assertFalse(storage.contains("a.b"));
		assertFalse(storage.contains("x.y"));
		assertEquals(0, storage.getKeys().size());
		assertTrue(root.isEmpty());
	}

	@Test
	void testModifyCallbackAlwaysCalled() {
		modifyCalled.set(false);

		storage.edit()
		       .setValue("test.value", 42, Integer.class)
		       .apply();

		assertTrue(modifyCalled.get());
	}

	@Test
	void testOverwriteValue() {
		storage.edit()
		       .setValue("setting.mode", "easy", String.class)
		       .apply();

		storage.edit()
		       .setValue("setting.mode", "hard", String.class)
		       .apply();

		assertEquals("hard", storage.getValue("setting.mode", String.class));
	}

	@Test
	void testKeysAreUnmodifiable() {
		storage.edit()
		       .setValue("one", 1, Integer.class)
		       .setValue("two", 2, Integer.class)
		       .apply();

		var keys = storage.getKeys();
		assertThrows(UnsupportedOperationException.class, () -> keys.add("three"));
	}

	@Test
	void testScanKeysAfterManualModification() {
		// 手动写节点（模拟旧数据）
		MutableObjectElement obj = MutableElement.object();
		obj.add("@value", MutableElement.number(99));
		root.add("legacy", obj);

		// 新建 storage 会自动 scanKeys
		ElementPreferenceStorage storage2 =
			new ElementPreferenceStorage(root, () -> {
			});

		assertEquals(99, storage2.getValue("legacy", Integer.class));
		assertTrue(storage2.contains("legacy"));
	}
}
