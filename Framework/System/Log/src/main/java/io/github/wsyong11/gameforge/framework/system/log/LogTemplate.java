package io.github.wsyong11.gameforge.framework.system.log;

import io.github.wsyong11.gameforge.framework.annotation.CallerSensitive;
import io.github.wsyong11.gameforge.framework.system.log.templete.TemplateValueProvider;
import io.github.wsyong11.gameforge.util.NumberUtils;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class LogTemplate {
	@NotNull
	public static TemplateValueProvider lazy(@NotNull TemplateValueProvider supplier) {
		Objects.requireNonNull(supplier, "supplier is null");
		return supplier;
	}

	@NotNull
	public static TemplateValueProvider lazy(@Nullable Object object) {
		return lazy(() -> object);
	}

	@CallerSensitive
	@NotNull
	public static TemplateValueProvider currentStackTrace() {
		Thread thread = Thread.currentThread();
		StackTraceElement[] stackTrace = thread.getStackTrace();
		return () -> Arrays
			.stream(stackTrace)
			.map(e -> "\tat " + e)
			.collect(Collectors.joining("\n"));
	}

	// -------------------------------------------------------------------------------------------------------------- //

	/*
data: 8192 bytes; offset: [0, 512) 512 bytes;
	 | 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F | ANSI             |
0000 | 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 | ................ |
000F | 00 00 00 00 00 00 00 00 00                      | ................ |
     |                Folded 412 bytes                 |

data: 42 bytes; offset: [0, 512) 512 bytes;
	 | 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F | ANSI             |
     |                     *empty*                     |
	 */
	@NotNull
	public static TemplateValueProvider hexView(
		byte @NotNull [] data,
		int offset,
		int length,
		int column,
		int maxRow,
		boolean showInfo,
		@Nullable Charset charset
	) {
		return () -> {
			StringBuilder sb = new StringBuilder();

			if (showInfo)
				sb.append("data: ")
					.append(data.length)
					.append(" bytes; offset: [")
					.append(offset)
					.append(", ")
					.append(offset + length)
					.append(") ")
					.append(length)
					.append(" bytes;\n");

			int offsetListWidth = NumberUtils.digitLength((long) maxRow * column);

			sb.append(" ".repeat(offsetListWidth)).append(" | ");
			for (int i = 0; i < column; i++)
				sb.append(String.format("%02X ", i));
			sb.append('|');

			if (charset != null) {
				String charsetName = charset.displayName(Locale.ROOT);
				sb.append(' ')
					.append(charsetName)
					.append(" ".repeat(column - charsetName.length() + 1))
					.append('|');
			}

			if (data.length == 0)
				return sb;

			for (int i = offset, row = 0; i < Math.min(offset + length, data.length); i += column, row++) {
				sb.append('\n');

				if (row >= maxRow) {
					String message = "Folded %d bytes".formatted(data.length - offset - i);
					int len = column * 3 - message.length();
					sb.append(" ".repeat(offsetListWidth));
					sb.append(" | ");
					sb.append(message);
					sb.append(" ".repeat(len));
					sb.append('|');
					break;
				}

				sb.append(String.format("%0" + offsetListWidth + "X", i))
					.append(" | ");

				for (int v = 0; v < column; v++) {
					int index = i + v;

					if (index >= data.length)
						sb.append("   ");
					else
						sb.append(String.format("%02X ", data[index]));
				}

				sb.append('|');

				if (charset == null)
					continue;

				sb.append(' ');

				sb.append(".".repeat(column));
				// TODO: 2026/02/59 Charset display

				sb.append(" |");
			}

			return sb;
		};
	}
}
