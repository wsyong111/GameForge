package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResourcePathTest {
	@Test
	void testOf_empty() {
		ResourcePath p = ResourcePath.of("");
		assertTrue(p.isEmpty());
		assertFalse(p.isDirectory());
	}

	@Test
	void testOf_root() {
		ResourcePath p1 = ResourcePath.of("/");
		ResourcePath p2 = ResourcePath.of("\\");
		assertTrue(p1.isRoot());
		assertTrue(p2.isRoot());
	}

	@Test
	void testOf_normalPath() {
		ResourcePath p = ResourcePath.of("a/b/c");
		assertEquals(3, p.length());
		assertEquals("c", p.getName());
		assertFalse(p.isDirectory());
	}

	@Test
	void testOf_directory() {
		ResourcePath p = ResourcePath.of("a/b/c/");
		assertTrue(p.isDirectory());
	}

	@Test
	void testJoin_string() {
		ResourcePath base = ResourcePath.of("a/b");
		ResourcePath r = base.join("c/d");

		assertEquals("a/b/c/d", r.toString().replace("\\", "/"));
		assertEquals(4, r.length());
	}

	@Test
	void testStartsWith() {
		ResourcePath p = ResourcePath.of("a/b/c");

		assertTrue(p.startsWith("a/b"));
		assertFalse(p.startsWith("b/c"));
	}

	@Test
	void testEndsWith() {
		ResourcePath p = ResourcePath.of("a/b/c");

		assertTrue(p.endsWith("b/c"));
		assertTrue(p.endsWith("c"));
		assertFalse(p.endsWith("a/b"));
	}

	@Test
	void testParent() {
		ResourcePath p = ResourcePath.of("a/b/c");
		ResourcePath parent = p.parent();

		assertEquals("a/b/", parent.toString());
	}

	@Test
	void testSubPath() {
		ResourcePath p = ResourcePath.of("a/b/c/d");

		ResourcePath sub = p.subPath(1, 3);
		assertEquals("b/c/", sub.toString());
	}

	@Test
	void testRelativeToPrefix() {
		ResourcePath p = ResourcePath.of("a/b/c/d");
		ResourcePath base = ResourcePath.of("a/b");

		ResourcePath rel = p.relativeToPrefix(base, true);
		assertEquals("c/d", rel.toString());
	}

	@Test
	void testLowerUpper() {
		ResourcePath p = ResourcePath.of("A/B/C");

		assertEquals("a/b/c", p.lower().toString());
		assertEquals("A/B/C", p.upper().toString());
	}

	@Test
	void testWithName() {
		ResourcePath p = ResourcePath.of("a/b/c");

		ResourcePath changed = p.withName("d");
		assertEquals("a/b/d", changed.toString());
	}

	@Test
	void testExtension() {
		ResourcePath p = ResourcePath.of("a/b/test.txt");
		assertEquals("txt", p.getExtension());
	}

	@Test
	void testEqualsAndHash() {
		ResourcePath p1 = ResourcePath.of("a/b/c");
		ResourcePath p2 = ResourcePath.of("a/b/c");

		assertEquals(p1, p2);
		assertEquals(p1.hashCode(), p2.hashCode());
	}

	@Test
	void testCompareTo() {
		ResourcePath a = ResourcePath.of("a/b");
		ResourcePath b = ResourcePath.of("a/c");

		assertTrue(a.compareTo(b) < 0);
	}

	@Test
	void testToArray() {
		ResourcePath p = ResourcePath.of("a/b/c");

		String[] arr = p.toArray();
		assertArrayEquals(new String[]{"a", "b", "c"}, arr);
	}
}
