package io.github.wsyong11.gameforge.framework.system.resource.v2.pack;

import io.github.wsyong11.gameforge.framework.system.resource.ResourcePath;
import io.github.wsyong11.gameforge.framework.system.resource.v2.Resource;

import java.util.List;
import java.util.Map;

public abstract class FlatResourcePack<T> extends AbstractResourcePack {
	private volatile Map<ResourcePath, AssetsResource> resourceMap;
	private volatile Map<ResourcePath, List<ResourcePath>> fileTree;
	private volatile boolean loaded;

protected static abstract class FlatResource implements Resource{

}
}
