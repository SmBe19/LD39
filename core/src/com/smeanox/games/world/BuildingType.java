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
	oilrig,
	oilplant,
	gasrig,
	gasplant,
	coalmine,
	coalplant,
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
