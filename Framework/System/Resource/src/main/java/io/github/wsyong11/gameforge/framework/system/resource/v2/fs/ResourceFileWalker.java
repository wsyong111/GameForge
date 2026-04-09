package io.github.wsyong11.gameforge.framework.system.resource.v2.fs;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class ResourceFileWalker {
	public static void walk(@NotNull ResourceFileSystem fs, @NotNull ResourcePath path, int maxDepth, @NotNull ResourceWalkVisitor visitor) throws IOException {
		Objects.requireNonNull(fs, "fs is null");
		Objects.requireNonNull(visitor, "visitor is null");
		Objects.requireNonNull(path, "path is null");

		ResourceFileWalker walker = new ResourceFileWalker(fs, maxDepth);
		Result walkResult = walker.walk(path);
		do {
			ResourcePath walkingPath = walkResult.getPath();
			IOException exception = walkResult.getException();

			ResourceWalkVisitor.VisitResult visitResult = switch (walkResult.getType()) {
				case FILE -> {
					if (exception == null) {
						yield visitor.visitFile(walkingPath.toFile());
					} else {
						yield visitor.visitFileFailed(walkingPath.toFile(), exception);
					}
				}

				case ENTER_DIR -> visitor.preVisitDirectory(walkingPath.toDirectory());
				case LEAVE_DIR -> visitor.postVisitDirectory(walkingPath.toDirectory(), exception);
			};

			if (visitResult == ResourceWalkVisitor.VisitResult.TERMINATE) {
				return;
			} else if (visitResult == ResourceWalkVisitor.VisitResult.SKIP_SUBTREE) {
				walker.skipDir();
			}

			walkResult = walker.next();
		} while (walkResult != null);
	}

	private final ResourceFileSystem fs;
	private final int maxDepth;

	private final Deque<StackItem> stack;

	public ResourceFileWalker(@NotNull ResourceFileSystem fs, int maxDepth) {
		Objects.requireNonNull(fs, "fs is null");

		if (maxDepth < 0)
			throw new IllegalArgumentException("Max depth cannot be negative");

		this.fs = fs;
		this.maxDepth = maxDepth;

		this.stack = new ArrayDeque<>();
	}

	@NotNull
	protected Result visit(@NotNull ResourcePath path) {
		Objects.requireNonNull(path, "path is null");

		int depth = this.stack.size();
		if (depth >= this.maxDepth || !this.fs.isDirectory(path))
			return new Result(ResultType.FILE, path);

		List<ResourcePath> children;
		try {
			children = this.fs.list(path);
		} catch (IOException e) {
			return new Result(ResultType.FILE, path, e);
		}

		Iterator<ResourcePath> childrenIterator = children
			.stream()
			.sorted()
			.iterator();

		this.stack.push(new StackItem(path, childrenIterator));

		return new Result(ResultType.ENTER_DIR, path);
	}

	@NotNull
	public Result walk(@NotNull ResourcePath path) {
		return this.visit(path);
	}

	@Nullable
	public Result next() {
		StackItem item = this.stack.peek();
		if (item == null)
			return null;

		Iterator<ResourcePath> iterator = item.getChildren();
		if (!iterator.hasNext()) {
			this.stack.pop();
			return new Result(ResultType.LEAVE_DIR, item.getPath());
		}

		return this.visit(iterator.next());
	}

	public void skipDir() {
		StackItem item = this.stack.peek();
		if (item != null)
			item.skip();
	}

	private static class StackItem {
		private final ResourcePath path;
		private Iterator<ResourcePath> children;

		private StackItem(@NotNull ResourcePath path, @NotNull Iterator<ResourcePath> children) {
			Objects.requireNonNull(path, "path is null");
			Objects.requireNonNull(children, "children is null");

			this.path = path;
			this.children = children;
		}

		@NotNull
		public ResourcePath getPath() {
			return this.path;
		}

		@NotNull
		public Iterator<ResourcePath> getChildren() {
			return this.children;
		}

		public void skip() {
			this.children = Collections.emptyIterator();
		}
	}

	public static class Result {
		private final ResultType type;
		private final ResourcePath path;
		private final IOException exception;

		public Result(@NotNull ResultType type, @NotNull ResourcePath path, @Nullable IOException exception) {
			Objects.requireNonNull(type, "type is null");
			Objects.requireNonNull(path, "path is null");

			this.type = type;
			this.path = path;
			this.exception = exception;
		}

		public Result(@NotNull ResultType type, @NotNull ResourcePath path) {
			this(type, path, null);
		}

		@NotNull
		public ResultType getType() {
			return this.type;
		}

		@NotNull
		public ResourcePath getPath() {
			return this.path;
		}

		@Nullable
		public IOException getException() {
			return this.exception;
		}
	}

	public enum ResultType {
		ENTER_DIR,
		LEAVE_DIR,
		FILE
	}
}
