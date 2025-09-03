package io.github.wsyong11.gameforge.game.common;

public enum GameSide {
	CLIENT,
	SERVER;

	public boolean isServer() {
		return this == SERVER;
	}

	public boolean isClient() {
		return this == CLIENT;
	}
}
