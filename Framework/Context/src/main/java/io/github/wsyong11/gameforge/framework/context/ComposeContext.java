package io.github.wsyong11.gameforge.framework.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ComposeContext extends Context {
	private final Map<Class<? extends Context>, Context> contextMap;

	private final boolean childDebug;

	public ComposeContext(boolean debug, @NotNull Map<Class<? extends Context>, Context> contextMap) {
		super(debug);
		Objects.requireNonNull(contextMap, "contextMap is null");

		this.contextMap = Map.copyOf(contextMap);

		for (Map.Entry<Class<? extends Context>, Context> entry : this.contextMap.entrySet()) {
			Class<? extends Context> type = entry.getKey();
			Context value = entry.getValue();
			if (!type.isInstance(value))
				throw new IllegalArgumentException("Context type " + type.getName() + " is not an instance of " + value);
		}

		this.childDebug = this.contextMap
			.values()
			.stream()
			.anyMatch(Context::isDebug);
	}

	@NotNull
	public List<Context> getChild() {
		return List.copyOf(this.contextMap.values());
	}

	@Override
	public boolean isDebug() {
		return super.isDebug() || this.childDebug;
	}

	public boolean isChildDebug() {
		return this.childDebug;
	}

	public boolean isSelfDebug() {
		return super.isDebug();
	}

	@SuppressWarnings("unchecked")
	@Nullable
	@Override
	public <T extends Context> T asUnsafe(@NotNull Class<T> type) {
		Objects.requireNonNull(type, "type is null");

		Context context = this.contextMap.get(type);
		return context != null ? (T) context : super.asUnsafe(type);
	}

}
