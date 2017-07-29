package com.smeanox.games.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.smeanox.games.world.Planet;

public class PlanetWidget extends TextButton {

	private final Planet planet;

	public PlanetWidget(Planet planet, Skin skin) {
		super(planet.getName(), skin);
		this.planet = planet;
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		setText(planet.getName() + ": " + planet.getTotalDudes());
	}
}
