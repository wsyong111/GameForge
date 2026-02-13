package io.github.wsyong11.gameforge.assets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Assets {
	//	private static final String RESOURCE_LIST_LOCATION = "resource_list.txt";
	private static final String RESOURCE_LIST_LOCATION = "resource_list";

	private static final List<AssetsEntry> entries;

	static {
		ClassLoader classLoader = Assets.class.getClassLoader();
//		try (InputStream stream = classLoader.getResourceAsStream(RESOURCE_LIST_LOCATION)) {
//			if (stream == null)
//				throw new ExceptionInInitializerError("Cannot read " + RESOURCE_LIST_LOCATION);
//
//			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
//			entries = reader
//				.lines()
//				.filter(entry -> !entry.isEmpty())
//				.map(entry -> entry.split("\t"))
//				.filter(entry -> entry.length == 2)
//				.map(entry -> {
//					long size;
//					try {
//						size = Long.parseLong(entry[1]);
//					} catch (NumberFormatException e) {
//						return null;
//					}
//
//					return new AssetsEntry(classLoader, entry[0], size);
//				})
//				.filter(Objects::nonNull)
//				.toList();
//		} catch (IOException | UncheckedIOException e) {
//			throw new ExceptionInInitializerError(e);
//		}

		try (InputStream stream = classLoader.getResourceAsStream(RESOURCE_LIST_LOCATION)) {
			if (stream == null)
				throw new ExceptionInInitializerError("Cannot read " + RESOURCE_LIST_LOCATION);

			entries = List.copyOf(parseByteData(classLoader, new BufferedInputStream(stream)));
		} catch (IOException | UncheckedIOException e) {
			throw new ExceptionInInitializerError(e);
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
		return entries;
	}

	private Assets() { /* no-op */ }
}
