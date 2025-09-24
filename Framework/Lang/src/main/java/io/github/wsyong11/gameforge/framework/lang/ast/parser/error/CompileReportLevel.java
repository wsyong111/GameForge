package io.github.wsyong11.gameforge.framework.lang.ast.parser.error;

public enum CompileReportLevel {
	TIPS(0),
	WARNING(1),
	ERROR(2);

	private final int level;

	CompileReportLevel(int level) {
		this.level = level;
	}

	public int toInt() {
		return this.level;
	}
}
