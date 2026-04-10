package io.github.wsyong11.gameforge.framework.system.resource;

import io.github.wsyong11.gameforge.framework.system.resource.v2.fs.ResourceWalkVisitor;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ZipResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
//	private static final Logger LOGGER = Log.getLogger();

	public static void main(String[] args) throws IOException {
//		try {
//			LogManager.setAdapter("log4j2");
//			LogManager.bind(Main.class.getClassLoader());

		try (ResourcePack resourcePack = new ZipResourcePack(Path.of("D:/Projects/Java/GameForge/Stay True 1.21.5.zip"))) {
//				try (TimeIt _t = TimeIt.begin(LOGGER, "Load file")) {
			resourcePack.load(null);
//				}

			System.out.println(resourcePack.getSource());

			System.out.println(resourcePack.isDirectory(ResourcePath.of("assets/")));
			resourcePack.walk(ResourcePath.of("/"), Integer.MAX_VALUE, new ResourceWalkVisitor() {
				private int space = 0;

				@Override
				public @NotNull VisitResult preVisitDirectory(@NotNull ResourcePath path) throws IOException {
					System.out.println("|   ".repeat(this.space) + "|- " + path.getName() + "/");
					this.space++;
					return VisitResult.CONTINUE;
				}

				@Override
				public @NotNull VisitResult postVisitDirectory(@NotNull ResourcePath path, @Nullable IOException exception) throws IOException {
					this.space--;
					//				System.out.println("|   ".repeat(this.space));
					return VisitResult.CONTINUE;
				}

				@NotNull
				@Override
				public VisitResult visitFile(@NotNull ResourcePath path) throws IOException {
					System.out.println("|   ".repeat(this.space) + "|- " + path.getName() + "[" + resourcePack.getSize(path) + " B]");
					return VisitResult.CONTINUE;
				}
			});
		}
//		} finally {
//			LogManager.unbind(Main.class.getClassLoader());
//		}
	}
}