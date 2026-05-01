package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.impl.graph.SimpleResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceGraphTest {
	private ResourceGraph graph;

	@BeforeEach
	void setUp() {
		graph = new SimpleResourceGraph();
	}

	// ----------------------------
	// 基础 put / get
	// ----------------------------

	@Test
	void testPutAndGet() {
		ResourcePath path = ResourcePath.of("/a");
		Resource resource = mockResource("R1");

		graph.put(path, resource);

		assertEquals(resource, graph.get(path));
	}

	@Test
	void testGetLatestResource() {
		ResourcePath path = ResourcePath.of("/a");

		Resource r1 = mockResource("R1");
		Resource r2 = mockResource("R2");

		graph.put(path, r1);
		graph.put(path, r2);

		assertEquals(r2, graph.get(path));
	}

	@Test
	void testGetAll() {
		ResourcePath path = ResourcePath.of("/a");

		Resource r1 = mockResource("R1");
		Resource r2 = mockResource("R2");

		graph.put(path, r1);
		graph.put(path, r2);

		List<Resource> all = graph.getAll(path);

		assertEquals(2, all.size());
		assertTrue(all.contains(r1));
		assertTrue(all.contains(r2));
	}

	// ----------------------------
	// exist / isDir / isEntry
	// ----------------------------

	@Test
	void testExistsAfterPut() {
		ResourcePath path = ResourcePath.of("/a");
		graph.put(path, mockResource("R1"));

		assertTrue(graph.exists(path));
	}

	@Test
	void testIsFile() {
		ResourcePath path = ResourcePath.of("/a");
		graph.put(path, mockResource("R1"));

		assertTrue(graph.isFile(path));
		assertFalse(graph.isDir(path));
	}

	// ----------------------------
	// remove entry
	// ----------------------------

	@Test
	void testRemoveSingleEntry() {
		ResourcePath path = ResourcePath.of("/a");
		Resource r1 = mockResource("R1");

		graph.put(path, r1);

		boolean removed = graph.remove(path, r1);

		assertTrue(removed);
		assertNull(graph.get(path));
	}

	@Test
	void testRemoveWrongResourceFails() {
		ResourcePath path = ResourcePath.of("/a");

		graph.put(path, mockResource("R1"));

		boolean removed = graph.remove(path, mockResource("R2"));

		assertFalse(removed);
	}

	// ----------------------------
	// remove full path
	// ----------------------------

	@Test
	void testRemovePath() {
		ResourcePath path = ResourcePath.of("/a");

		graph.put(path, mockResource("R1"));

		boolean removed = graph.remove(path);

		assertTrue(removed);
		assertNull(graph.get(path));
	}

	// ----------------------------
	// clear
	// ----------------------------

	@Test
	void testClear() {
		graph.put(ResourcePath.of("/a"), mockResource("R1"));
		graph.put(ResourcePath.of("/b"), mockResource("R2"));

		graph.clear();

		assertEquals(0, graph.size());
		assertTrue(graph.listAllFlat().isEmpty());
	}

	// ----------------------------
	// size
	// ----------------------------

	@Test
	void testSize() {
		graph.put(ResourcePath.of("/a"), mockResource("R1"));
		graph.put(ResourcePath.of("/a"), mockResource("R2"));

		assertEquals(2, graph.size());
	}

	@Test
	void testTreeMapHierarchyBuild() {
		SimpleResourceGraph graph = new SimpleResourceGraph();

		ResourcePath root = ResourcePath.of("/");
		ResourcePath a = ResourcePath.of("/a");
		ResourcePath b = ResourcePath.of("/a/b");

		graph.put(a, mockResource("A"));
		graph.put(b, mockResource("B"));

		List<ResourcePath> rootChildren = graph.listChildren(root);
		assertNotNull(rootChildren);
		assertTrue(rootChildren.contains(a));

		List<ResourcePath> aChildren = graph.listChildren(a);
		assertNotNull(aChildren);
		assertTrue(aChildren.contains(b));
	}

	@Test
	void testRemoveDirectoryRecursive() {
		SimpleResourceGraph graph = new SimpleResourceGraph();

		ResourcePath a = ResourcePath.of("/a");
		ResourcePath b = ResourcePath.of("/a/b");
		ResourcePath c = ResourcePath.of("/a/b/c");

		graph.put(b, mockResource("B"));
		graph.put(c, mockResource("C"));

		boolean removed = graph.remove(a.toDirectory());

		assertTrue(removed);

		assertNull(graph.get(b));
		assertNull(graph.get(c));

		assertTrue(graph.listChildren(a) == null || graph.listChildren(a).isEmpty());
	}

	@Test
	void testRemoveDirDoesNotAffectSiblings() {
		SimpleResourceGraph graph = new SimpleResourceGraph();

		ResourcePath a = ResourcePath.of("/a");
		ResourcePath b = ResourcePath.of("/b");

		ResourcePath a1 = ResourcePath.of("/a/1");

		graph.put(a1, mockResource("A1"));
		graph.put(b, mockResource("B"));

		graph.remove(a.toDirectory());

		assertNull(graph.get(a1));   // 被删
		assertNotNull(graph.get(b)); // 不影响
	}

	@Test
	void testCopySharesResourcesMap() {
		SimpleResourceGraph original = new SimpleResourceGraph();

		ResourcePath path = ResourcePath.of("/a");
		Resource r1 = mockResource("R1");

		original.put(path, r1);

		SimpleResourceGraph copy = (SimpleResourceGraph) original.copy();

		// 修改原 graph
		original.remove(path, r1);

		// ❗ copy 应该不受影响，但实际上可能会受影响
		assertNotNull(copy.get(path));
	}

	@Test
	void testCopyTreeMapIsolation() {
		SimpleResourceGraph original = new SimpleResourceGraph();

		ResourcePath a = ResourcePath.of("/a");
		ResourcePath b = ResourcePath.of("/a/b");

		original.put(a, mockResource("A"));
		original.put(b, mockResource("B"));

		SimpleResourceGraph copy = (SimpleResourceGraph) original.copy();

		// 修改 original tree
		original.remove(a.toDirectory());

		// copy 不应该被影响
		List<ResourcePath> children = copy.listChildren(a);
		assertNotNull(children);
	}

	@Test
	void testCopyConsistency() {
		SimpleResourceGraph original = new SimpleResourceGraph();

		ResourcePath a = ResourcePath.of("/a");
		ResourcePath b = ResourcePath.of("/a/b");

		Resource r1 = mockResource("R1");
		Resource r2 = mockResource("R2");

		original.put(a, r1);
		original.put(b, r2);

		SimpleResourceGraph copy = (SimpleResourceGraph) original.copy();

		assertEquals(original.size(), copy.size());
		assertEquals(original.get(a), copy.get(a));
		assertEquals(original.get(b), copy.get(b));
	}

	@NotNull
	private Resource mockResource(@NotNull String id) {
		Objects.requireNonNull(id, "id is null");

		ResourcePath path = ResourcePath.of(id);
		return new Resource() {
			@NotNull
			@Override
			public InputStream openStream() throws IOException {
				return InputStream.nullInputStream();
			}

			@NotNull
			@Override
			public ResourcePath getPath() {
				return path;
			}

			@Nullable
			@Override
			public ResourcePack getSource() {
				return null;
			}

			@Override
			public long getSize() {
				return -1L;
			}
		};
	}
}
