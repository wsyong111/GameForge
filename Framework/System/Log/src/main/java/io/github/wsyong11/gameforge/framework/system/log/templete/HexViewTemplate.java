package io.github.wsyong11.gameforge.framework.system.log.templete;

import io.github.wsyong11.gameforge.util.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HexViewTemplate implements TemplateValueProvider {
	private static final char BORDER_VERTICAL = '|';
	private static final char UNKNOWN_CHAR = '?';
	private static final char UNPRINTABLE_CHAR = '.';

	private static final char[] HEX = "0123456789ABCDEF".toCharArray();

	@NotNull
	public static HexViewTemplate.Builder builder(byte @Nullable [] data) {
		return new Builder(data);
	}

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
			sb.append(NumberUtils.toHexFast(i, 2));
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

	private void printLine(@NotNull StringBuilder sb, int fromIndex, int toIndex, int addressLineWidth, int @Nullable [] parsedCodePoints) {
		Objects.requireNonNull(sb, "sb is null");

		sb.append(NumberUtils.toHexFast(fromIndex, addressLineWidth));
		sb.append(' ');
		sb.append(BORDER_VERTICAL);
		sb.append(' ');

		for (int i = fromIndex; i < toIndex; i++) {
			int data = this.data[i] & 0xFF;

			sb.append(HEX[(data >>> 4) & 0xF]);
			sb.append(HEX[data & 0xF]);

			if (i < toIndex - 1)
				sb.append(' ');
		}

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
			sb.appendCodePoint(parsedCodePoints[i]);

		if (hexCount < this.offsetColumn) {
			int spaceCount = this.offsetColumn - hexCount;
			sb.append(" ".repeat(spaceCount));
		}

		sb.append(' ');
		sb.append(BORDER_VERTICAL);
	}

	private void printFolded(@NotNull StringBuilder sb, int remaining, int addressLineWidth) {
		Objects.requireNonNull(sb, "sb is null");

		sb.append(" ".repeat(addressLineWidth));
		sb.append(' ');
		sb.append(BORDER_VERTICAL);
		sb.append(' ');

		String message = "Folded " + remaining + " bytes";

		int fullWidth = (this.offsetColumn - 1) * 3 + 2;
		float spaceWidth = (fullWidth - message.length()) / 2.0F;

		sb.append(" ".repeat((int) spaceWidth));
		sb.append(message);
		sb.append(" ".repeat((int) Math.ceil(spaceWidth)));

		sb.append(' ');
		sb.append(BORDER_VERTICAL);
	}

	private int[] parseDataAscii(int fromIndex, int toIndex) {
		int[] result = new int[toIndex - fromIndex];

		for (int i = fromIndex; i < toIndex; i++) {
			int data = this.data[i] & 0xFF;

			if (data >= 0x20 && data <= 0x7E)
				result[i] = (char) data;
			else
				result[i] = UNPRINTABLE_CHAR;
		}

		return result;
	}

	private int[] parseDataCharset(@NotNull Charset charset, int fromIndex, int toIndex) {
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

			if (!Character.isISOControl(codePoint) && Character.getType(codePoint) != Character.FORMAT) {
				list.add(codePoint);
			} else {
				list.add((int) UNPRINTABLE_CHAR);
			}

			for (int i = 0; i < byteCount - 1; i++)
				list.add((int) ' ');
		}

		return list.stream().mapToInt(i -> i).toArray();
	}

	private int[] parseData(@NotNull Charset charset, int fromIndex, int toIndex) {
		Objects.requireNonNull(charset, "charset is null");

		if (charset == StandardCharsets.US_ASCII)
			return this.parseDataAscii(fromIndex, toIndex);
		return this.parseDataCharset(charset, fromIndex, toIndex);
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

		int[] parsedCodePoints;
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


	public static class Builder {
		private final byte @Nullable [] data;

		private int offset = 0;
		private int length = -1;
		private int maxAddressLine = 128;
		private int offsetColumn = 16;
		private boolean showInfo = true;
		@Nullable
		private Charset charset = StandardCharsets.US_ASCII;

		public Builder(byte @Nullable [] data) {
			this.data = data;
		}

		@NotNull
		public Builder offset() {
			return this.offset(0);
		}

		@NotNull
		public Builder offset(int offset) {
			this.offset = offset;
			return this;
		}

		@NotNull
		public Builder length() {
			return this.length(-1);
		}

		@NotNull
		public Builder length(int length) {
			this.length = length;
			return this;
		}

		@NotNull
		public Builder maxAddressLine(int maxAddressLine) {
			this.maxAddressLine = maxAddressLine;
			return this;
		}

		@NotNull
		public Builder offsetColumn(int offsetColumn) {
			this.offsetColumn = offsetColumn;
			return this;
		}

		@NotNull
		public Builder showInfo() {
			return this.showInfo(true);
		}

		public Builder showInfo(boolean showInfo) {
			this.showInfo = showInfo;
			return this;
		}

		@NotNull
		public Builder ascii() {
			return this.charset(StandardCharsets.US_ASCII);
		}

		@NotNull
		public Builder charset(@Nullable Charset charset) {
			this.charset = charset;
			return this;
		}

		@NotNull
		public HexViewTemplate build() {
			return new HexViewTemplate(
				this.data,
				this.offset,
				this.length < 0 ? (this.data == null ? 0 : this.data.length) : this.length,
				this.maxAddressLine,
				this.offsetColumn,
				this.showInfo,
				this.charset
			);
		}
	}
}
