package io.github.wsyong11.gameforge.framework.system.graphic.core.info;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import org.semver4j.Semver;

import java.util.List;

public interface RenderBackedInfo {
	@NotNull
	String getDriver();

	@NotNull
	Semver getVersion();

	@NotNull
	String getVendor();

	@NotNull
	@UnmodifiableView
	List<RenderDeviceInfo> getDevices();
}
