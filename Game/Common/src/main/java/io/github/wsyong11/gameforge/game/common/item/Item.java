package io.github.wsyong11.gameforge.game.common.item;

import org.jetbrains.annotations.NotNull;

public interface Item extends ItemLike {
	@Override
	@NotNull
	default Item asItem() {
		return this;
	}
}
