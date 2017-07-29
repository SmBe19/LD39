package com.smeanox.games.world;

import com.smeanox.games.util.BuildingConfigReader;

public enum BuildingType {
	city,
	street,
	oilplant,
	gasplant,
	coalplant,
	oilrig,
	gasrig,
	coalmine,
	solarplant,
	solarfactory,
	spaceport,
	propellantfactory,
	waterplant,
	metalmine,
	;

	public final BuildingConfig config;

	BuildingType() {
		this.config = BuildingConfigReader.get(this.name());
		if (this.config == null) {
			throw new RuntimeException("Building configuration " + this.name() + " not found");
		}
	}
}
