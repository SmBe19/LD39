package com.smeanox.games.world;

import com.smeanox.games.util.SpaceShipConfigReader;

public enum SpaceShipType {
	small,
	medium,
	large,
	giant,
	;

	public final SpaceShipConfig config;

	SpaceShipType() {
		this.config = SpaceShipConfigReader.get(this.name());
		if (this.config == null) {
			throw new RuntimeException("Space ship configuration " + this.name() + " not found");
		}
	}
}
