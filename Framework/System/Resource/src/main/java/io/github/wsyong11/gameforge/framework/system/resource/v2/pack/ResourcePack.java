package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

public interface ResourcePack extends Closeable {
	@Nullable
	URI getSource();

	void load(@NotNull LoadListener listener) throws IOException;

	boolean exist(@NotNull ResourcePath path);

	// 文件不存在 抛出错误
	long size(@NotNull ResourcePath path) throws IOException;

	// 文件不存在 抛出错误
	@NotNull
	InputStream open(@NotNull ResourcePath path) throws IOException;

	@NotNull
	List<ResourcePath> list();

	interface LoadListener {
		// < 0 Unknown
		void onStart(int total);

		void onSuccess(@NotNull ResourcePath path);

		void onFailure(@NotNull ResourcePath path, @NotNull Throwable e);

		void onComplete();
	}
}
