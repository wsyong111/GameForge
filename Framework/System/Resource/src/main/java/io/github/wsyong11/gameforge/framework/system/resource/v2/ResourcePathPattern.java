package io.github.wsyong11.gameforge.framework.system.resource.v2;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ResourcePathPattern {
	/*
pattern   := segment ( "/" segment )*

segment   := literal | variable

variable  := "{" name ( ":" type )? "}" | "{" name "..." "}"

literal   := any non-special string

type      := "int" | "word" | "uuid" | "path"
	 */
	@NotNull
	public static ResourcePathPattern compile(@NotNull String pattern) {
		Objects.requireNonNull(pattern, "pattern is null");

		if (pattern.isEmpty())
			throw new IllegalArgumentException("Pattern cannot be empty");

		for (String segment : pattern.split(ResourcePath.SEPARATOR)) {
			if (segment.isEmpty())
				continue;


		}
	}

	@NotNull
	private static ResourcePathPattern.Segment parseSegment(@NotNull String segment) {
		Objects.requireNonNull(segment, "segment is null");

		if (!segment.startsWith("{") || !segment.endsWith("}"))
			return;

		int length = segment.length();
		String body = segment.substring(1, length - 1);

		int parameterSep = body.indexOf(':');
		if (parameterSep == -1) {
			return new
		}
	}

	protected interface Segment {

	}

	protected class LiteralSegment implements Segment {

	}

	protected class VariableSegment implements Segment {

	}
}
