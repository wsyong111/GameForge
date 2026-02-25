package io.github.wsyong11.gameforge.framework.system.audio;

import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypes;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.OGGAudioDecoder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class Main {
	public static void main(String[] args) throws Throwable {
		try (InputStream oggInput = Files.newInputStream(Path.of("D:/Projects/Java/GameForge/Assets/src/main/resources/assets/game/sound/test_bgm.ogg"))) {
			AudioDecoder.DecodeInfo decodeInfo = new AudioDecoder.DecodeInfo() {
				@Override
				public @NotNull InputStream openStream() {
					return oggInput;
				}

				@Override
				public @NotNull @UnmodifiableView Set<AudioDecodeHint> getHints() {
					return Set.of(AudioDecodeHint.STREAMING);
				}

				@Override
				public @NotNull MimeType getMime() {
					return MimeTypes.Audio.OGG;
				}
			};

			try (AudioDecoder decoder = OGGAudioDecoder.FACTORY.create(decodeInfo)) {
				AudioMetadata metadata = decoder.getMetadata();
				System.out.println(metadata);
			}
		}
	}
}