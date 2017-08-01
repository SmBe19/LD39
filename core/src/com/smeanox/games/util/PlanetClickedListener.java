package com.smeanox.games.util;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.smeanox.games.world.Planet;

public abstract class PlanetClickedListener implements EventListener {

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof PlanetClickedEvent)) {
			return false;
		}
		PlanetClickedEvent planetEvent = (PlanetClickedEvent) event;
		clicked(planetEvent, planetEvent.getPlanet());
		return true;
	}

	public abstract void clicked(PlanetClickedEvent event, Planet planet);
}
