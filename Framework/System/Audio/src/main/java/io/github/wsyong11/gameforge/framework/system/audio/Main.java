package io.github.wsyong11.gameforge.framework.system.audio;

import io.github.wsyong11.gameforge.framework.Identifier;
import io.github.wsyong11.gameforge.framework.mime.MimeType;
import io.github.wsyong11.gameforge.framework.mime.MimeTypes;
import io.github.wsyong11.gameforge.framework.system.audio.audio.AudioMetadata;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecodeHint;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.AudioDecoder;
import io.github.wsyong11.gameforge.framework.system.audio.audio.decoder.ogg.OggAudioDecoderFactory;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.manage.DefaultResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.framework.system.resource.pack.AssetsResourcePack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.io.InputStream;
import java.util.Set;

public class Main {
	public static void main(String[] args) throws Throwable {
		ClassLoader classLoader = Main.class.getClassLoader();
		LogManager.setAdapter("log4j2");
		LogManager.bind(classLoader);

		ResourceManager rs = new DefaultResourceManager(ResourcePath.of("assets"));
		rs.addPack(new AssetsResourcePack("game"));
		rs.reload();

		try (InputStream oggInput = rs.getResource(Identifier.withDefaultNamespace("sound/test_bgm.ogg")).openStream()) {
			AudioDecoder.DecodeInfo decodeInfo = new AudioDecoder.DecodeInfo() {
				@Override
				public @NotNull InputStream openStream() {
					return oggInput;
				}

				@Override
				public @NotNull @UnmodifiableView Set<AudioDecodeHint> getHints() {
					return Set.of();
				}

				@Override
				public @NotNull MimeType getMime() {
					return MimeTypes.Audio.OGG;
				}
			};

			try (AudioDecoder decoder = OggAudioDecoderFactory.INSTANCE.get().create(decodeInfo)) {
				AudioMetadata metadata = decoder.getMetadata();
				System.out.println(metadata);
			}
		}
	}
}