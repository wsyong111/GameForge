package io.github.wsyong11.gameforge.game.loader;

import io.github.wsyong11.gameforge.framework.bootstrap.Bootstrap;
import io.github.wsyong11.gameforge.framework.system.log.Log;
import io.github.wsyong11.gameforge.framework.system.log.Logger;
import io.github.wsyong11.gameforge.framework.system.log.core.LogLevel;
import io.github.wsyong11.gameforge.framework.system.log.core.LogManager;
import io.github.wsyong11.gameforge.game.common.core.StartupConfig;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.help.HelpFormatter;
import org.apache.commons.cli.help.TextHelpAppendable;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainLoader {
	private static final Logger LOGGER = Log.getLogger();

	private static final Path DEFAULT_LOG_DIR = Path.of("./log/");
	private static final Path DEFAULT_TEMP_DIR = Path.of(System.getProperty("java.io.tmpdir", "./tmp/"));
	private static final Path DEFAULT_MOD_DIR = Path.of("./mods/");
	private static final Path DEFAULT_CONFIG_DIR = Path.of("./config/");
	private static final Path DEFAULT_CRASH_REPORT_DIR = Path.of("./crash_report/");
	private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.INFO;

	static {
		LogManager.setAdapter("log4j2");
		LogManager.bind(MainLoader.class.getClassLoader());
	}

	public static void main(String[] args) {
		Objects.requireNonNull(args, "args is null");

		CommandLineParser commandLineParser = new DefaultParser();

		try {
			mainUnsafe(commandLineParser.parse(CommandLineOptions.OPTIONS, args));
		} catch (ParseException e) {
			LOGGER.error("Cannot parse command line arguments", e);
			System.exit(1);
		}
	}

	private static void mainUnsafe(@NotNull CommandLine commandLine) throws ParseException {
		Objects.requireNonNull(commandLine, "commandLine is null");

		if (commandLine.hasOption(CommandLineOptions.HELP)) {
			helpMain();
			return;
		}

		String gameTypeId = commandLine.getOptionValue(CommandLineOptions.GAME_TYPE);
		GameType gameType = GameType.parse(gameTypeId);
		if (gameType == null)
			throw new ParseException("Unknown game type: " + gameTypeId);

		Path logDir = commandLine.getParsedOptionValue(CommandLineOptions.LOG_DIR, DEFAULT_LOG_DIR);
		Path tempDir = commandLine.getParsedOptionValue(CommandLineOptions.TEMP_DIR, DEFAULT_TEMP_DIR);
		Path modDir = commandLine.getParsedOptionValue(CommandLineOptions.MOD_DIR, DEFAULT_MOD_DIR);
		Path configDir = commandLine.getParsedOptionValue(CommandLineOptions.CONFIG_DIR, DEFAULT_CONFIG_DIR);
		Path crashReportDir = commandLine.getParsedOptionValue(CommandLineOptions.CRASH_REPORT_DIR, DEFAULT_CRASH_REPORT_DIR);

		LogLevel logLevel = Objects.requireNonNullElse(LogLevel.parse(commandLine.getOptionValue(CommandLineOptions.LOG_LEVEL)), DEFAULT_LOG_LEVEL);

		boolean debug = commandLine.hasOption(CommandLineOptions.DEBUG);

		List<Path> modJarPaths = Arrays
			.stream(Objects.requireNonNullElse(commandLine.getOptionValues(CommandLineOptions.MOD_JAR), ArrayUtils.EMPTY_STRING_ARRAY))
			.map(Path::of)
			.toList();

		boolean safeMode = commandLine.hasOption(CommandLineOptions.SAFE_MODE);

		try (Bootstrap<StartupConfig> bootstrap = Bootstrap
			.<StartupConfig>builder()
			.mainClass(gameType.getFactory())
			.logDir(logDir)
			.enableDebug(debug)
			.logLevel(logLevel)
			.config(new StartupConfig(
				logDir,
				tempDir,
				modDir,
				configDir,
				crashReportDir,
				debug,
				modJarPaths,
				safeMode
			))
			.build()
		) {
			bootstrap.start();
		} catch (Exception e) {
			LOGGER.error("Fatal error", e);
		}
	}

	private static void helpMain() {
		StringBuilder output = new StringBuilder();

		TextHelpAppendable appendable = new TextHelpAppendable(output);
		appendable.getTextStyleBuilder()
		          .setIndent(4)
		          .setMaxWidth(80);

		HelpFormatter formatter = HelpFormatter
			.builder()
			.setHelpAppendable(appendable)
			.get();

		try {
			formatter.printHelp("app", null, CommandLineOptions.OPTIONS, null, true);
		} catch (IOException e) {
			LOGGER.error("Cannot print command line help", e);
		}

		LOGGER.warn("\n{}", output);
	}
}