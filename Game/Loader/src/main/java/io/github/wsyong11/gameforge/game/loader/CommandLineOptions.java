package io.github.wsyong11.gameforge.game.loader;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

import java.nio.file.Path;

public interface CommandLineOptions {
	Option HELP = Option
		.builder()
		.option("h")
		.longOpt("help")
		.desc("Print command line in logger")
		.type(boolean.class)
		.hasArg(false)
		.since("0.0.0")
		.get();

	Option GAME_TYPE = Option
		.builder()
		.longOpt("type")
		.desc("Game startup type")
		.type(String.class)
		.hasArg(true)
		.argName("TYPE")
		.since("0.0.0")
		.get();

	// -------------------------------------------------------------------------------------------------------------- //

	Option TEMP_DIR = Option
		.builder()
		.longOpt("temp-dir")
		.desc("Cache folder location")
		.type(Path.class)
		.hasArg(true)
		.argName("DIR")
		.since("0.0.0")
		.get();

	Option MOD_DIR = Option
		.builder()
		.longOpt("mod-dir")
		.desc("Mod folder location")
		.type(Path.class)
		.hasArg(true)
		.argName("DIR")
		.since("0.0.0")
		.get();

	Option CONFIG_DIR = Option
		.builder()
		.longOpt("config-dir")
		.desc("Game configuration folder location")
		.type(Path.class)
		.hasArg(true)
		.argName("DIR")
		.since("0.0.0")
		.get();

	Option CRASH_REPORT_DIR = Option
		.builder()
		.longOpt("crash-report-dir")
		.desc("Game crash report folder location")
		.type(Path.class)
		.hasArg(true)
		.argName("DIR")
		.since("0.0.0")
		.get();

	Option LOG_DIR = Option
		.builder()
		.longOpt("log-dir")
		.desc("Log folder location")
		.type(Path.class)
		.hasArg(true)
		.argName("DIR")
		.since("0.0.0")
		.get();

	// -------------------------------------------------------------------------------------------------------------- //

	Option LOG_LEVEL = Option
		.builder()
		.longOpt("log-level")
		.desc("Game minimum log level, allow trace, debug, info, warn and error")
		.type(String.class)
		.hasArg(true)
		.argName("LEVEL")
		.since("0.0.0")
		.get();

	Option DEBUG = Option
		.builder()
		.longOpt("debug")
		.desc("Enable debug mode")
		.type(boolean.class)
		.hasArg(false)
		.since("0.0.0")
		.get();

//	Option DEBUG_FUTURES = Option
//		.builder()
//		.longOpt("enable-debug-future")
//		.desc("Enable debug futures")
//		.type(String.class)
//		.hasArgs()
//		.argName("FUTURE")
//		.get();

	// -------------------------------------------------------------------------------------------------------------- //

	Option MOD_JAR = Option
		.builder()
		.longOpt("mod-jar")
		.desc("Add mod jar with the command line")
		.type(Path.class)
		.hasArgs()
		.argName("JAR")
		.since("0.0.0")
		.get();

	Option SAFE_MODE = Option
		.builder()
		.longOpt("safe-mode")
		.desc("Enable safe mode, external mods will be forcibly disabled")
		.type(boolean.class)
		.hasArg(false)
		.since("0.0.0")
		.get();

	// -------------------------------------------------------------------------------------------------------------- //

	Options OPTIONS = new Options()
		.addOption(HELP)
		.addOption(GAME_TYPE)
		.addOption(TEMP_DIR)
		.addOption(MOD_DIR)
		.addOption(CONFIG_DIR)
		.addOption(CRASH_REPORT_DIR)
		.addOption(LOG_DIR)
		.addOption(LOG_LEVEL)
		.addOption(DEBUG)
//		.addOption(DEBUG_FUTURES)
		.addOption(MOD_JAR);
}
