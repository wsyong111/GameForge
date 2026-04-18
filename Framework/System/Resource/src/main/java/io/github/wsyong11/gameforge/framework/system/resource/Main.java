package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceGraph;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.manage.impl.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.AssetsResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ZipResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.ResourceTransformer;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.TransformContext;
import io.github.wsyong11.gameforge.framework.system.resource.v2.transform.TransformedResource;
import io.github.wsyong11.gameforge.util.StreamUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Main {
//	private static final Logger LOGGER = Log.getLogger();

	private static final Path RESOURCE_PACK_FOLDER = Path.of("./resource_pack_gen");


	static final int PACK_COUNT = 200;      // 资源包数量
	static final int FILES_PER_PACK = 5000; // 每个包文件数
	static final int MAX_FILE_SIZE = 4096;  // 单文件最大字节（模拟贴图/JSON）

	static final String[] NAMESPACES = {
		"minecraft", "moda", "modb", "test", "create", "milthm", "ciallo", "ae2"
	};

	public static void main1(String[] args) throws Exception {
		Path output = Paths.get("resource_pack_gen");
		Files.createDirectories(output);

		Random random = new Random(42); // 固定种子，方便复现

		long start = System.currentTimeMillis();

		for (int i = 0; i < PACK_COUNT; i++) {
			Path zipPath = output.resolve("pack_" + i + ".zip");

			try (ZipOutputStream zos = new ZipOutputStream(
				new BufferedOutputStream(Files.newOutputStream(zipPath)))) {

				// ⚡ 关闭压缩 = 生成速度更快（压测加载，不测压缩）
				zos.setLevel(Deflater.NO_COMPRESSION);

				Set<String> generatedPath = new HashSet<>();
				for (int j = 0; j < FILES_PER_PACK; j++) {
					// 🎯 1. 随机 namespace
					String ns = NAMESPACES[random.nextInt(NAMESPACES.length)];

					// 🎯 2. 10% 概率制造冲突文件（所有 pack 都有）
					String path;
					if (random.nextDouble() < 0.1) {
						path = "assets/" + ns + "/common/shared_" + random.nextInt(6000) + ".json";
					} else {
						path = "assets/" + ns + "/textures/block/file_" + j + ".json";
					}

					if (!generatedPath.add(path))
						continue;

					zos.putNextEntry(new ZipEntry(path));

					// 🎯 3. 随机大小内容（模拟真实资源）
					int size = random.nextInt(MAX_FILE_SIZE) + 16;
					byte[] data = new byte[size];
					random.nextBytes(data);

					zos.write(data);
					zos.closeEntry();
				}
			}

			if (i % 10 == 0) {
				System.out.println("Generated pack " + i);
			}
		}

		long end = System.currentTimeMillis();
		System.out.println("Done in " + (end - start) + " ms");
	}

	public static void main(String[] args) throws Throwable {
		Thread.sleep(1000);
		ClassLoader classLoader = Main.class.getClassLoader();
		try {
			LogManager.setAdapter("log4j2");
			LogManager.bind(classLoader).getRootLoggerConfig().setLevel(LogLevel.DEBUG);
			main();
		} finally {
			LogManager.unbind(classLoader);
		}
	}

	private static void main() throws Throwable {
		try (ResourceManager manager = new DefaultResourceManager()) {
			AssetsResourcePack assetsPack = new AssetsResourcePack();
			manager.registerResourcePack(assetsPack);
			manager.setPackPriority(assetsPack, -1);

			try (Stream<Path> fileList = Files.list(RESOURCE_PACK_FOLDER)) {
				for (Path path : StreamUtils.toIterable(fileList)) {
					System.out.println(path);
					if (!Files.isRegularFile(path))
						continue;

					if (!path.getFileName().toString().endsWith(".zip"))
						continue;

					manager.registerResourcePack(new ZipResourcePack(path));
				}
			}

			manager.addExtension(ResourceTransformer.TYPE, new Transformer());

			manager.reload();

			Thread.sleep(5000);

//			ResourceFileSystem fs = manager.getFileSystem();
//			try (InputStream stream = fs.openStream(ResourcePath.of("assets/game/shader/config/test.json"))) {
//				BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
//				reader.lines()
//				      .forEach(System.out::println);
//			}
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
			if (!"test".equals(path.getExtension()))
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