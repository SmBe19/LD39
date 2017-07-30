package com.smeanox.games.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.StringBuilder;
import com.smeanox.games.world.Planet;

public class PlanetWidget extends TextButton {

	private final Planet planet;

	public PlanetWidget(Planet planet, Skin skin) {
		super(planet.getName(), skin);
		this.planet = planet;
		getLabel().setText(createCurrentText());

		planet.addListener(new Planet.PlanetListener() {
			@Override
			public void planetNameChanged(Planet planet) {
				getLabel().setText(createCurrentText());
			}

			@Override
			public void totalDudesChanged(Planet planet) {
				getLabel().setText(createCurrentText());
			}
		});
	}

	private StringBuilder createCurrentText(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(planet.getName()).append(": ")
				.append(planet.getTotalDudes())
				.append("/").append(planet.getDudesCapacity());
		return stringBuilder;
	}
}
