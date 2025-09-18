package io.github.wsyong11.gameforge.framework.platform;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Platform {
	public static final OS os;

	static {
		String name = System.getProperty("os.name").toLowerCase();

		if (name.contains("win"))
			os = OS.WINDOWS;
		else if (name.contains("nux") || name.contains("nix"))
			os = OS.LINUX;
		else if (name.contains("mac"))
			os = OS.MAC;
		else if (name.contains("sunos"))
			os = OS.SOLARIS;
		else
			os = OS.UNKNOWN;
	}

	public enum OS {
		WINDOWS,
		LINUX,
		MAC,
		SOLARIS,
		UNKNOWN
	}
}
