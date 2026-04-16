package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceFileSystem;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.AssetsResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ZipResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.TransformContext;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.TransformedResource;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
//	private static final Logger LOGGER = Log.getLogger();

	public static void main(String[] args) throws Throwable {
		ClassLoader classLoader = Main.class.getClassLoader();
		try {
			LogManager.setAdapter("log4j2");
			LogManager.bind(classLoader);
			main();
		} finally {
			LogManager.unbind(classLoader);
		}
	}

	private static void main() throws Throwable {
		try (ResourceManager manager = new DefaultResourceManager()) {
			AssetsResourcePack assetsPack = new AssetsResourcePack();
			manager.registerResourcePack(assetsPack);

			Path zipPath = Path.of("D:\\Projects\\Java\\GameForge\\Stay True 1.21.5.zip");
			if (Files.exists(zipPath))
				manager.registerResourcePack(new ZipResourcePack(zipPath));

			manager.setPackPriority(assetsPack, -1);

			manager.addExtension(ResourceTransformer.TYPE, new Transformer());

			manager.reload();

			ResourceFileSystem fs = manager.getFileSystem();
			try (InputStream stream = fs.openStream(ResourcePath.of("assets/game/shader/config/test.json"))) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
				reader.lines()
				      .forEach(System.out::println);
			}
		}
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

	private static class Transformer implements ResourceTransformer {
		@Override
		public void transform(@NotNull Resource resource, @NotNull TransformContext context) {
			ResourcePath path = resource.getPath();
			if (!".test".equals(path.getExtension()))
				return;

			context.replaceResource(path, res -> new TransformedResource(res) {
				@NotNull
				@Override
				protected InputStream transformStream(@NotNull InputStream stream) {
					return new SequenceInputStream(stream, new ByteArrayInputStream("EOF".getBytes(StandardCharsets.UTF_8)));
				}
			});

			context.addResource(path.transformName(n -> n + ".src"), resource);
		}
	}
}