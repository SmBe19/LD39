package com.smeanox.games.util;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.smeanox.games.world.Planet;

public class PlanetClickedEvent extends Event {
	private Planet planet;

	public PlanetClickedEvent() {
	}

	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
	}
}
