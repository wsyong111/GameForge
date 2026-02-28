package io.github.wsyong11.gameforge.framework.system.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypes;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.ogg.OggAudioDecoderFactory;
import io.github.wsyong11.gameforge.framework.system.log.LogTemplate;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.log.templete.TemplateValueProvider;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.pack.AssetsResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Main {
//	public static void main(String[] args) throws Throwable {
//		ClassLoader classLoader = Main.class.getClassLoader();
//		LogManager.setAdapter("log4j2");
//		LogManager.bind(classLoader);
//
//		ResourceManager rs = new DefaultResourceManager(ResourcePath.of("assets"));
//		rs.addPack(new AssetsResourcePack("game"));
//		rs.reload();
//
//		JFrame frame = new JFrame("Sound test");
//		frame.setVisible(true);
//
//		try (InputStream oggInput = rs.getResource(Identifier.withDefaultNamespace("sound/out.ogg")).openStream()) {
//			AudioDecoder.DecodeInfo decodeInfo = new AudioDecoder.DecodeInfo() {
//				@Override
//				public @NotNull InputStream openStream() {
//					return oggInput;
//				}
//
//				@Override
//				public @NotNull @UnmodifiableView Set<AudioDecodeHint> getHints() {
//					return Set.of();
//				}
//
//				@Override
//				public @NotNull MimeType getMime() {
//					return MimeTypes.Audio.OGG;
//				}
//			};
//
//			try (AudioDecoder decoder = OggAudioDecoderFactory.INSTANCE.get().create(decodeInfo)) {
//				AudioMetadata metadata = decoder.getMetadata();
//				System.out.println(metadata);
//
//				int sampleRate = metadata.getSampleRate();
//				int channels = metadata.getChannels();
//				long totalSamples = metadata.getTotalFrames();
//
//				long seekOff = 2;
//				long seekOffS = sampleRate * seekOff;
//
//				AudioFormat format = new AudioFormat(
//					sampleRate,
//					16,            // 转成 16bit
//					channels,
//					true,          // signed
//					false          // little endian
//				);
//
//				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
//				SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
//				try {
//					line.open(format, channels * sampleRate * 2 * 2);
//					line.start();
//
//					int sr = sampleRate * 5;
//					int bufSize = channels * sr;
//					FloatBuffer buffer = FloatBuffer.wrap(new float[bufSize]);
//					byte[] tempBuf = new byte[bufSize * 2];
//
//					while (true) {
//						buffer.clear();
//						buffer.position(0);
//
//						System.out.println("Begin decode");
//						long start = System.nanoTime();
//						int consumed = decoder.decode(buffer, sr);
//						if (consumed == -1)
//							break;
//
//						buffer.flip();
//
//						int available = consumed * channels;
//						int idx = 0;
//						for (int i = 0; i < available; i++) {
//							float f = buffer.get(i);
//
//							// 防止爆音
//							if (f > 1f) f = 1f;
//							if (f < -1f) f = -1f;
//
//							short s = (short) (f * 32767f);
//
//							tempBuf[idx++] = (byte) (s & 0xff);
//							tempBuf[idx++] = (byte) ((s >> 8) & 0xff);
//						}
//						System.out.printf("Decoded took %dms%n",
//							TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
//
//						line.write(tempBuf, 0, tempBuf.length);
//					}
//				} finally {
//					line.drain();
//					line.stop();
//					line.close();
//					frame.dispose();
//				}
//			}
//		}
//	}

	public static void main(String[] args) {
		Random random = new Random();

/*
data: 71 bytes; offset: [0, 71) 71 bytes;
   | 00 01 02 03 04 05 06 07 08 09 0A 0B 0C 0D 0E 0F | UTF-8            |
00 | E2 87 9A 32 4C 69 A8 D5 0C 91 89 98 20 92 B9 21 |                  |
10 | 0B 55 96 94 4B 4F D2 16 03 FF 36 A7 92 06 30 F7 |                  |
20 | E3 95 1A 3B 02 5C 7C DB 41 C7 71 EB 7F C9 90 21 |                  |
30 | 07 86 12 DD B6 5F C7 F6 25 07 03 65 82 73 AF 3C |                  |
40 | AD 56 76 6C FF 85 DA                            |                  |
 */

		byte[] data = new byte[71];
		random.nextBytes(data);

		TemplateValueProvider view = LogTemplate.hexView(
			data,
			6,
			24,
			8,
			64,
			true,
			StandardCharsets.UTF_8);
		System.out.println(view.get());
	}
}