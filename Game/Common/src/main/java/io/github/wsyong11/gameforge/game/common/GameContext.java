package io.github.wsyong11.gameforge.game.common;

import io.github.wsyong11.gameforge.framework.event.IEventBus;
import io.github.wsyong11.gameforge.framework.lifecycle.LifecycleProvider;
import io.github.wsyong11.gameforge.framework.system.resource.manage.ResourceManager;
import io.github.wsyong11.gameforge.game.common.registry.RegistryManager;
import io.github.wsyong11.gameforge.game.common.tick.TickManager;
import io.github.wsyong11.gameforge.game.common.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.List;

public interface GameContext extends LifecycleProvider {
	@NotNull
	GameSide getSide();

	@NotNull
	@UnmodifiableView
	List<World> getWorlds();

	@NotNull
	RegistryManager getRegistry();

	@NotNull
	IEventBus getEventBus();

	@NotNull
	TickManager getTickManager();

	@NotNull
	ResourceManager getResourceManager();
}
