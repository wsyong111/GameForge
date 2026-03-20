package io.github.wsyong11.gameforge.framework.system.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.system.audio.audio.Audio;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioManager;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.ogg.OggAudioDecoderFactory;
import io.github.wsyong11.gameforge.framework.system.audio.audio.ex.AudioDecodeException;
import io.github.wsyong11.gameforge.framework.system.audio.audio.stream.AudioStream;
import io.github.wsyong11.gameforge.framework.system.audio.impl.openal.OpenALAudioEngine;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.pack.AssetsResourcePack;
import io.github.wsyong11.gameforge.util.concurrent.signal.Notifier;
import io.github.wsyong11.gameforge.util.concurrent.signal.ThreadSignal;
import org.jetbrains.annotations.NotNull;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.nio.FloatBuffer;
import java.util.concurrent.TimeUnit;

public class Main {
	private static final Identifier TEST_SOUND = Identifier.withDefaultNamespace("sound/out.ogg");

	public static void main(String[] args) throws Throwable {
		ClassLoader classLoader = Main.class.getClassLoader();
		LogManager.setAdapter("log4j2");
		LogManager.bind(classLoader).getRootLoggerConfig().setLevel(LogLevel.VERBOSE);

		try (ResourceManager rs = new DefaultResourceManager(ResourcePath.of("assets"))) {
			rs.addPack(new AssetsResourcePack("game"));
			rs.reload();

			AudioSystem audioSystem = AudioSystem.init(() -> OpenALAudioEngine::new, rs);
			AudioManager audioManager = audioSystem.getAudioManager();
			audioManager.registerAudioDecoder(OggAudioDecoderFactory.INSTANCE.get(), 1);

			Audio audio = audioManager.getAudio(TEST_SOUND);
			System.out.println("AUDIO: " + audio);

			ThreadSignal n = new ThreadSignal();
			if (audio != null) {
				audio.registerStatusCallback(new Audio.StatusCallback() {
					@Override
					public void onReady() {
						System.out.println("METADATA: " + audio.getMetadata());
						n.set();
					}
				});

				n.await();
				play(audio);
			}


//			try (DefaultAudioManager manager = new DefaultAudioManager(rs)) {
//				manager.registerAudioDecoder(OggAudioDecoderFactory.INSTANCE.get(), 0);
//
//				Audio audio = manager.getAudio(TEST_SOUND);
//				System.out.println(audio);
//			}
		} finally {
			AudioSystem.shutdown();
			LogManager.unbind(classLoader);
		}
	}

	private static void play(@NotNull Audio audio) throws LineUnavailableException, AudioDecodeException {
		try (AudioStream stream = audio.newStream()) {
			AudioMetadata metadata = audio.getMetadata();

			int frameRate = metadata.getFrameRate();
			int channels = metadata.getChannels();
			long totalSamples = metadata.getTotalFrames();

			long seekOff = 2;
//			long seekOffS = sampleRate * seekOff;

			AudioFormat format = new AudioFormat(
				metadata.getSampleRate(),
				16,            // 转成 16bit
				channels,
				true,          // signed
				false          // little endian
			);

			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			SourceDataLine line = (SourceDataLine) javax.sound.sampled.AudioSystem.getLine(info);
			try {
				line.open(format, channels * frameRate * 2 * 2);
				line.start();

				int sr = frameRate * 5;
				int bufSize = channels * sr;
				FloatBuffer buffer = FloatBuffer.wrap(new float[bufSize]);
				byte[] tempBuf = new byte[bufSize * 2];

				while (true) {
					buffer.clear();
					buffer.position(0);

					System.out.println("Begin decode");
					long start = System.nanoTime();
					int consumed = stream.read(buffer, sr);
					if (consumed == -1)
						break;

					buffer.flip();

					int available = consumed * channels;
					int idx = 0;
					for (int i = 0; i < available; i++) {
						float f = buffer.get(i);

						// 防止爆音
						if (f > 1f) f = 1f;
						if (f < -1f) f = -1f;

						short s = (short) (f * 32767f);

						tempBuf[idx++] = (byte) (s & 0xff);
						tempBuf[idx++] = (byte) ((s >> 8) & 0xff);
					}
					System.out.printf("Decoded took %dms%n",
						TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));

					line.write(tempBuf, 0, tempBuf.length);
				}
			} finally {
				line.drain();
				line.stop();
				line.close();
			}
		}
	}

//		FloatBuffer buf = FloatBuffer.wrap(new float[]{
//			0.0F, 0.05F,
//			0.1F, 0.15F,
//			0.2F, 0.25F,
//			0.3F, 0.35F,
//			0.4F, 0.45F,
//			0.5F, 0.55F,
//			0.6F, 0.65F,
//			0.7F, 0.75F,
//			0.8F, 0.85F,
//			0.9F, 0.95F,
//			0.0F
//		});
//
//		PCMArray array = new FloatBufferPCMArray(buf, 2, 1);
//		System.out.println(array.getFrames());
//
//		FloatBuffer a = FloatBuffer.wrap(new float[]{999.0F, 999.0F, 999.0F, 999.0F});
//		System.out.println(array.getFrame(a, 1, Integer.MAX_VALUE));
//		System.out.println(Arrays.toString(a.array()));

//		array.sampleStream(0)
//			.forEachOrdered(System.out::println);

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
//				int sampleRate = metadata.getFrameRate();
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
}

