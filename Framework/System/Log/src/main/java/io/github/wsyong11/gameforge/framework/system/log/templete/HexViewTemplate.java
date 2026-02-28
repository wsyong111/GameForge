package io.github.wsyong11.gameforge.framework.system.log.templete;

import io.github.wsyong11.gameforge.util.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HexViewTemplate implements TemplateValueProvider {
	private static final char BORDER_VERTICAL = '|';
	private static final char UNKNOWN_CHAR = '?';
	private static final char UNPRINTABLE_CHAR = '.';

	private final byte[] data;

	private final int offset;
	private final int length;
	private final int maxAddressLine;
	private final int offsetColumn;
	private final boolean showInfo;
	private final Charset charset;

	private final int fromIndex;
	private final int toIndex;

	public HexViewTemplate(
		byte @Nullable [] data,
		int offset,
		int length,
		int maxAddressLine,
		int offsetColumn,
		boolean showInfo,
		@Nullable Charset charset
	) {
		this.data = data;

		this.offset = Math.max(0, offset);
		this.length = Math.max(0, length);
		this.maxAddressLine = Math.max(0, maxAddressLine);
		this.offsetColumn = Math.max(1, offsetColumn);
		this.showInfo = showInfo;
		this.charset = charset;

		this.fromIndex = this.offset;
		this.toIndex = this.offset + this.length;
	}

	// data: 0 bytes; offset: [0, 2) 2 bytes;
	private void printInfo(@NotNull StringBuilder sb) {
		Objects.requireNonNull(sb, "sb is null");

		sb.append("data: ");
		sb.append(this.data == null ? "<null>" : this.data.length);
		sb.append(" bytes; offset: [");
		sb.append(this.fromIndex);
		sb.append(", ");
		sb.append(this.toIndex);
		sb.append(") ");
		sb.append(this.length);
		sb.append(" bytes;\n");
	}

	private void printHeader(@NotNull StringBuilder sb, int addressLineWidth) {
		Objects.requireNonNull(sb, "sb is null");

		sb.append(" ".repeat(addressLineWidth + 1));
		sb.append(BORDER_VERTICAL);
		sb.append(' ');

		for (int i = 0; i < this.offsetColumn; i++) {
			sb.append(String.format("%02X", i));
			if (i < this.offsetColumn - 1)
				sb.append(' ');
		}

		sb.append(' ');
		sb.append(BORDER_VERTICAL);

		if (this.charset == null)
			return;

		sb.append(' ');

		String charsetName = this.charset.displayName();
		int charsetNameLength = charsetName.length();
		sb.append(charsetName);
		sb.append(" ".repeat(Math.max(0, this.offsetColumn - charsetNameLength)));
		sb.append(' ');
		sb.append(BORDER_VERTICAL);
	}

	private void printLine(@NotNull StringBuilder sb, int fromIndex, int toIndex, int addressLineWidth, @Nullable List<Integer> parsedCodePoints) {
		Objects.requireNonNull(sb, "sb is null");

		sb.append(String.format("%0" + addressLineWidth + "X", fromIndex));
		sb.append(' ');
		sb.append(BORDER_VERTICAL);
		sb.append(' ');

		for (int i = fromIndex; i < toIndex; i++) {
			byte data = this.data[i];

			sb.append(String.format("%02X", data));
			if (i < toIndex - 1)
				sb.append(' ');
		}

		// 如果结尾有空区域则填充
		int hexCount = toIndex - fromIndex;
		if (hexCount < this.offsetColumn) {
			int spaceCount = this.offsetColumn - hexCount;
			sb.append("   ".repeat(spaceCount));
		}

		sb.append(' ');
		sb.append(BORDER_VERTICAL);

		if (parsedCodePoints == null)
			return;

		sb.append(' ');

		for (int i = fromIndex; i < toIndex; i++)
			sb.appendCodePoint(parsedCodePoints.get(i));

		sb.append(' ');
		sb.append(BORDER_VERTICAL);
	}

	private void printFolded(@NotNull StringBuilder sb, int remaining, int addressLineWidth) {
		Objects.requireNonNull(sb, "sb is null");

		sb.append(" ".repeat(addressLineWidth));
		sb.append(' ');
		sb.append(BORDER_VERTICAL);
		sb.append(' ');

		String message = "Folded %d bytes".formatted(remaining);

		int fullWidth = (this.offsetColumn - 1) * 3 + 2;
		int spaceWidth = (fullWidth - message.length()) / 2;

		sb.append(" ".repeat(spaceWidth));
		sb.append(message);
		sb.append(" ".repeat(spaceWidth));

		if (spaceWidth % 2 != 0)
			sb.append(' ');

		sb.append(' ');
		sb.append(BORDER_VERTICAL);
	}

	@NotNull
	private List<Integer> parseData(@NotNull Charset charset, int fromIndex, int toIndex) {
		Objects.requireNonNull(charset, "charset is null");

		CharsetDecoder decoder = charset
			.newDecoder()
			.onMalformedInput(CodingErrorAction.REPORT)
			.onUnmappableCharacter(CodingErrorAction.REPORT);

		ByteBuffer buffer = ByteBuffer.wrap(this.data);
		buffer.position(fromIndex);
		buffer.limit(toIndex);

		CharBuffer charBuffer = CharBuffer.allocate(2);

		List<Integer> list = new ArrayList<>();

		while (buffer.hasRemaining()) {
			int startPos = buffer.position();

			int codePoint = UNKNOWN_CHAR;
			int byteCount = 1;

			for (int i = 1; i <= 4 && startPos + i <= toIndex; i++) {
				buffer.position(startPos);
				buffer.limit(startPos + i);

				charBuffer.clear();
				decoder.reset();

				CoderResult result = decoder.decode(buffer, charBuffer, false);

				if (charBuffer.position() > 0 && !result.isUnmappable() && !result.isMalformed()) {
					byteCount = i;

					charBuffer.flip();
					codePoint = Character.codePointAt(charBuffer, 0);
					break;
				}
			}

			buffer.position(startPos + byteCount);
			buffer.limit(this.data.length);
			list.add(codePoint);

			for (int i = 0; i < byteCount - 1; i++)
				list.add((int) ' ');
		}

		return list;
	}

	@NotNull
	@Override
	public StringBuilder getValue() {
		StringBuilder builder = new StringBuilder(128);

		int dataFullLength = this.data == null ? 0 : this.data.length;
		int dataLength = Math.max(0, Math.min(this.length, dataFullLength - this.offset));

		int toIndexSafe = Math.min(this.toIndex, dataLength);
		int addressLineWidth = NumberUtils.hexDigitLength(toIndexSafe);

		if (this.showInfo)
			this.printInfo(builder);

		this.printHeader(builder, addressLineWidth);

		if (dataLength < this.fromIndex)
			return builder;

		builder.append('\n');


		List<Integer> parsedCodePoints;
		if (this.charset != null)
			parsedCodePoints = this.parseData(this.charset, this.fromIndex, toIndexSafe);
		else
			parsedCodePoints = null;

		for (int i = this.fromIndex, line = 0; i < toIndexSafe; i += this.offsetColumn, line++) {
			if (line > this.maxAddressLine) {
				int remaining = toIndexSafe - i;
				this.printFolded(builder, remaining, addressLineWidth);
				break;
			}

			this.printLine(
				builder,
				i,
				Math.min(i + this.offsetColumn, dataLength),
				addressLineWidth,
				parsedCodePoints
			);

			if (i < toIndexSafe - 1)
				builder.append('\n');
		}

		return builder;
	}
}
