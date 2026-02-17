package io.github.wsyong11.gameforge.framework.system.log.adapter.log4j2;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.Level;

@UtilityClass
public class VerboseLevel {
	public static final Level VERBOSE = Level.forName("VERBOSE", 700);
}
