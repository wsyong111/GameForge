package io.github.wsyong11.gameforge.framework.listener.list;

import io.github.wsyong11.gameforge.framework.listener.ListenerList;
import io.github.wsyong11.gameforge.util.Nameable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class NamedListenerList extends ListenerListWrapper implements Nameable {
	private final String name;

	public NamedListenerList(@NotNull ListenerList delegate, @NotNull String name) {
		super(
			Objects.requireNonNull(delegate, "delegate is null")
		);
		Objects.requireNonNull(name, "name is null");
		this.name = name;
	}

	@NotNull
	@Override
	public String getName() {
		return this.name;
	}
}
