package com.smeanox.games.world;

import com.smeanox.games.util.BuildingConfigReader;

public enum BuildingType {
	city,
	spaceport,
	propellantfactory,
	waterplant,
	metalmine,
	solarfactory,
	solarplant,
	coalmine,
	coalplant,
	oilrig,
	oilplant,
	gasrig,
	gasplant,
	street,
	;

	public final BuildingConfig config;

	BuildingType() {
		this.config = BuildingConfigReader.get(this.name());
		if (this.config == null) {
			throw new RuntimeException("Building configuration " + this.name() + " not found");
		}
	}
}
