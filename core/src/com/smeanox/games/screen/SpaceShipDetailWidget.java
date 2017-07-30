package com.smeanox.games.screen;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.StringBuilder;
import com.smeanox.games.Consts;
import com.smeanox.games.world.SpaceShip;
import com.smeanox.games.world.SpaceShipConfig;

public class SpaceShipDetailWidget extends Label {

	private SpaceShip spaceShip;
	private final SpaceShip.SpaceShipListener listener;

	public SpaceShipDetailWidget(Skin skin) {
		super("", skin);

		listener = new SpaceShip.SpaceShipListener() {
			@Override
			public void spaceShipStateChanged(SpaceShip spaceShip) {
				setText(getCurrentText());
			}

			@Override
			public void spaceShipNameChanged(SpaceShip spaceShip) {
				setText(getCurrentText());
			}

			@Override
			public void spaceShipFreightChanged(SpaceShip spaceShip) {
				setText(getCurrentText());
			}
		};
	}

	public SpaceShip getSpaceShip() {
		return spaceShip;
	}

	public void setSpaceShip(SpaceShip spaceShip) {
		if(this.spaceShip != null) {
			this.spaceShip.removeListener(listener);
		}

		this.spaceShip = spaceShip;

		this.spaceShip.addListener(listener);

		setText(getCurrentText());
	}

	private StringBuilder getCurrentText(){
		StringBuilder stringBuilder = new StringBuilder();
		SpaceShipConfig config = spaceShip.getType().config;
		float propellant = MathUtils.round(config.propellantPerWeightAndDistance * spaceShip.getWeight() * 100) / 100f;
		stringBuilder
				.append("Name: ").append(spaceShip.getName())
				.append("\nPosition: ").append(spaceShip.isInSpace() ? "Space" : spaceShip.getStart().getName())
				.append("\nSpeed: ").append(config.speed / Consts.UNIVERSE_STEP_SIZE * 1000f).append(" km/s")
				.append("\nMass empty: ").append(config.weight).append(" kg")
				.append("\nMass now: ").append(spaceShip.getWeight()).append(" kg")
				.append("\nPropellant: ").append(propellant).append(" /km")
				.append("\nCapacity total: ").append(config.capacity).append(" kg")
				.append("\nCapacity used: ").append(spaceShip.getWeight() - config.weight).append(" kg");
		return stringBuilder;
	}
}
