package io.github.wsyong11.gameforge.framework.system.resource.pack;

import io.github.wsyong11.gameforge.framework.dataflow.loader.DataLoader;
import io.github.wsyong11.gameforge.framework.dataflow.loader.SimpleDataLoader;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public abstract class AbstractResourcePack implements ResourcePack {
//	protected static class PackInfo implements ResourcePackInfo {
//		private static final DataLoader<PackInfo> LOADER = new SimpleDataLoader<PackInfo>()
//			.register(0, () -> () -> (node) -> {
//				return null;
//			});
//
//		@NotNull
//		public static PackInfo parse(@NotNull InputStream stream) throws IOException {
//			Objects.requireNonNull(stream, "stream is null");
//
////			LOADER.load(stream, )
//			// TODO: 2025/9/2
//			return null;
//		}
//	}
}
