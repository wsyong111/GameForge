package io.github.wsyong11.gameforge.framework.system.crashreport;

import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.*;

public class CrashReportComments {
	private static final Set<String> COMMENTS = new HashSet<>(Set.of(
		":(",
		":L",
		">.<",
		"This is not fun :L",
		"114514",
		"Ouch!",
		"Hmm, there seems to be something wrong",
		"Who made Creeper explode?",
		"A moth appears in the code"
	));

	private static final String EMPTY_COMMENT = "Comment not found :(";

	private static final Random RANDOM = new Random(System.nanoTime());

	@NotNull
	public static String get() {
		try {
			return COMMENTS
				.stream()
				.skip(RANDOM.nextInt(COMMENTS.size() - 1))
				.findFirst()
				.orElse(EMPTY_COMMENT);
		} catch (Throwable e) {
			return EMPTY_COMMENT;
		}
	}

	public static void add(@NotNull String comment) {
		Objects.requireNonNull(comment, "comment is null");
		COMMENTS.add(comment);
	}
}
