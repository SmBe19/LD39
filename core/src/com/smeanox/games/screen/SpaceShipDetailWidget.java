package com.smeanox.games.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.StringBuilder;
import com.smeanox.games.world.SpaceShip;
import com.smeanox.games.world.SpaceShipConfig;

public class SpaceShipDetailWidget extends Label {

	private SpaceShip spaceShip;
	private boolean wasInSpace;

	public SpaceShipDetailWidget(Skin skin) {
		super("", skin);
	}

	public SpaceShip getSpaceShip() {
		return spaceShip;
	}

	public void setSpaceShip(SpaceShip spaceShip) {
		this.spaceShip = spaceShip;

		setText(getCurrentText());
		wasInSpace = spaceShip.isInSpace();
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (wasInSpace != spaceShip.isInSpace()) {
			setText(getCurrentText());
			wasInSpace = spaceShip.isInSpace();
		}
	}

	private StringBuilder getCurrentText(){
		StringBuilder stringBuilder = new StringBuilder();
		SpaceShipConfig config = spaceShip.getType().config;
		stringBuilder
				.append("Position: ").append(spaceShip.isInSpace() ? "Space" : spaceShip.getStart().getName())
				.append("\nCapacity: ").append(config.capacity)
				.append("\nPropellant usage: ").append(config.propellantPerWeightAndDistance)
				.append("\nSpeed: ").append(config.speed);
		return stringBuilder;
	}
}
