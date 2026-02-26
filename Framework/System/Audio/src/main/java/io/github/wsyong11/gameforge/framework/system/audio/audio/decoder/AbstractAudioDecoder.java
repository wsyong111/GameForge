package io.github.wsyong11.gameforge.framework.system.audio.audio.decoder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractAudioDecoder implements AudioDecoder {
	private final DecodeInfo info;
	private final Set<AudioDecodeHint> activatedHints;

	public AbstractAudioDecoder(@NotNull DecodeInfo info) {
		Objects.requireNonNull(info, "info is null");

		this.info = info;

		Set<AudioDecodeHint> hints = this.info.getHints();
		this.activatedHints = hints
			.stream()
			.filter(this::isSupportHint)
			.collect(Collectors.toUnmodifiableSet());
	}

	@NotNull
	protected DecodeInfo getInfo() {
		return this.info;
	}

	protected boolean containHint(@NotNull AudioDecodeHint hint) {
		Objects.requireNonNull(hint, "hint is null");
		return this.activatedHints.contains(hint);
	}

	protected abstract boolean isSupportHint(@NotNull AudioDecodeHint hint);

	@NotNull
	@UnmodifiableView
	@Override
	public Set<AudioDecodeHint> getActivatedHints() {
		return this.activatedHints;
	}
}
