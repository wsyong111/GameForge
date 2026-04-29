package io.github.wsyong11.gameforge.framework.system.resource.v2.impl.query;

import com.google.common.collect.Streams;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;
import io.github.wsyong11.gameforge.framework.system.resource.v2.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.pack.ResourcePack;
import io.github.wsyong11.gameforge.framework.system.resource.v2.query.ResourceQuery;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StreamResourceQuery implements ResourceQuery {
	private final Stream<Resource> stream;

	public StreamResourceQuery(@NotNull Iterable<Resource> iterable) {
		Objects.requireNonNull(iterable, "iterable is null");
		this.stream = Streams.stream(iterable);
	}

	protected StreamResourceQuery(@NotNull Stream<Resource> stream) {
		Objects.requireNonNull(stream, "stream is null");
		this.stream = stream;
	}

	@NotNull
	@Override
	public ResourceQuery ofStartPath(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");
		return new StreamResourceQuery(this.stream.filter(res ->
			res.getPath().startsWith(path)));
	}

	@NotNull
	@Override
	public ResourceQuery ofPath(@NotNull Predicate<ResourcePath> condition) {
		Objects.requireNonNull(condition, "condition is null");
		return new StreamResourceQuery(this.stream.filter(res ->
			condition.test(res.getPath())));
	}

	@NotNull
	@Override
	public ResourceQuery ofPack(@NotNull Predicate<ResourcePack> condition) {
		Objects.requireNonNull(condition, "condition is null");
		return new StreamResourceQuery(this.stream.filter(res ->
			condition.test(res.getSource())));
	}

	@NotNull
	@Override
	public ResourceQuery ofPattern(@NotNull String pattern) {
		Objects.requireNonNull(pattern, "pattern is null");
		return this.ofPattern(Pattern.compile(pattern));
	}

	@NotNull
	@Override
	public ResourceQuery ofPattern(@NotNull Pattern pattern) {
		Objects.requireNonNull(pattern, "pattern is null");
		return new StreamResourceQuery(this.stream.filter(res ->
			pattern.matcher(res.getPath().toString()).matches()));
	}

	@NotNull
	@Override
	public ResourceQuery ofNamePattern(@NotNull String pattern) {
		Objects.requireNonNull(pattern, "pattern is null");
		return this.ofNamePattern(Pattern.compile(pattern));
	}

	@NotNull
	@Override
	public ResourceQuery ofNamePattern(@NotNull Pattern pattern) {
		Objects.requireNonNull(pattern, "pattern is null");
		return new StreamResourceQuery(this.stream.filter(res ->
			pattern.matcher(res.getPath().getName()).matches()));
	}

	@NotNull
	@Override
	public ResourceQuery ofPredicate(@NotNull Predicate<Resource> condition) {
		Objects.requireNonNull(condition, "condition is null");
		return new StreamResourceQuery(this.stream.filter(condition));
	}

	@NotNull
	@Override
	public Stream<Resource> stream() {
		return this.stream;
	}
}
