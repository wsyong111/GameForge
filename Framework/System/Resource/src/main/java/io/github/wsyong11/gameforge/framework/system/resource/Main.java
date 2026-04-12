package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.system.resource.v2.ByteArrayResource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.simple.SimpleResourceGraph;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class Main {
//	private static final Logger LOGGER = Log.getLogger();

	public static void main(String[] args) throws IOException {
//		try {
//			LogManager.setAdapter("log4j2");
//			LogManager.bind(Main.class.getClassLoader());

		ResourceGraph graph = new SimpleResourceGraph();
		graph.put(ResourcePath.of("path/to/example/test.txt"), new ByteArrayResource(ArrayUtils.EMPTY_BYTE_ARRAY, ResourcePath.ROOT));
		graph.put(ResourcePath.of("path/to/test/aaa.txt"), new ByteArrayResource(ArrayUtils.EMPTY_BYTE_ARRAY, ResourcePath.ROOT));
		graph.put(ResourcePath.of("path/to/info.bat"), new ByteArrayResource(ArrayUtils.EMPTY_BYTE_ARRAY, ResourcePath.ROOT));
		graph.put(ResourcePath.of("path/to/info.json"), new ByteArrayResource(ArrayUtils.EMPTY_BYTE_ARRAY, ResourcePath.ROOT));
		graph.put(ResourcePath.of("a.json"), new ByteArrayResource(ArrayUtils.EMPTY_BYTE_ARRAY, ResourcePath.ROOT));

		graph.remove(ResourcePath.of("path/to/info.json"));
		graph.remove(ResourcePath.of("path/to/example/"));
		graph.remove(ResourcePath.of("/"));

		printTree(graph, 0, ResourcePath.ROOT);

//		try (ResourcePack resourcePack = new ZipResourcePack(Path.of("D:/Projects/Java/GameForge/Stay True 1.21.5.zip"))) {
////				try (TimeIt _t = TimeIt.begin(LOGGER, "Load file")) {
//			resourcePack.load(null);
////				}
//
//			System.out.println(resourcePack.getSource());
//
//			System.out.println(resourcePack.isDirectory(ResourcePath.of("assets/")));
//			resourcePack.walk(ResourcePath.of("/"), Integer.MAX_VALUE, new ResourceWalkVisitor() {
//				private int space = 0;
//
//				@Override
//				public @NotNull VisitResult preVisitDirectory(@NotNull ResourcePath path) throws IOException {
//					System.out.println("|   ".repeat(this.space) + "|- " + path.getName() + "/");
//					this.space++;
//					return VisitResult.CONTINUE;
//				}
//
//				@Override
//				public @NotNull VisitResult postVisitDirectory(@NotNull ResourcePath path, @Nullable IOException exception) throws IOException {
//					this.space--;
//					//				System.out.println("|   ".repeat(this.space));
//					return VisitResult.CONTINUE;
//				}
//
//				@NotNull
//				@Override
//				public VisitResult visitFile(@NotNull ResourcePath path) throws IOException {
//					System.out.println("|   ".repeat(this.space) + "|- " + path.getName() + "[" + resourcePack.getSize(path) + " B]");
//					return VisitResult.CONTINUE;
//				}
//			});
//		}
//		} finally {
//			LogManager.unbind(Main.class.getClassLoader());
//		}
	}

	private static void printTree(@NotNull ResourceGraph graph, int tab, @NotNull ResourcePath path) {
		List<ResourcePath> list = graph.list(path);
		if (list == null)
			return;

		for (ResourcePath child : list) {
			boolean directory = child.isDirectory();

			System.out.println("|  ".repeat(tab) + "|- " + child.getName() + (directory ? "/" : ""));
			if (directory)
				printTree(graph, tab + 1, child);
		}
	}
}