package io.github.wsyong11.gameforge.assets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Assets {
	private static final String RESOURCE_LIST_LOCATION = "resource_list";

	private static volatile List<AssetsEntry> entries = null;

	public static void ensure() throws IOException {
		if (entries != null)
			return;

		synchronized (Assets.class) {
			if (entries != null)
				return;

			ClassLoader classLoader = Assets.class.getClassLoader();

			try (InputStream stream = classLoader.getResourceAsStream(RESOURCE_LIST_LOCATION)) {
				if (stream == null)
					throw new FileNotFoundException("Cannot read " + RESOURCE_LIST_LOCATION);

				entries = List.copyOf(parseByteData(classLoader, new BufferedInputStream(stream)));
			}
		}
	}

	@NotNull
	private static List<AssetsEntry> parseByteData(@NotNull ClassLoader classLoader, @NotNull InputStream stream) throws IOException {
		Objects.requireNonNull(classLoader, "classLoader is null");
		Objects.requireNonNull(stream, "stream is null");

		DataInputStream dataStream = new DataInputStream(stream);
		int entryCount = dataStream.readInt();
		if (entryCount < 0)
			throw new IOException("The entry count is negative");

		List<AssetsEntry> entries = new ArrayList<>(entryCount);

		for (int i = 0; i < entryCount; i++) {
			String path = dataStream.readUTF();
			long size = dataStream.readLong();
			if (size < 0)
				throw new IOException("The size is negative, at entry " + i);

			entries.add(new AssetsEntry(classLoader, path, size));
		}

		return entries;
	}

	@Unmodifiable
	@NotNull
	public static List<AssetsEntry> getEntries() {
		if (entries == null)
			throw new IllegalStateException("Assets list is not load");

		return entries;
	}

	private Assets() { /* no-op */ }
}
