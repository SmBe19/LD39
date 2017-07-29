package com.smeanox.games.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.StringBuilder;
import com.smeanox.games.world.SpaceShip;

public class ShipWidget extends TextButton {

	private final SpaceShip spaceShip;
	private boolean wasInSpace;

	public ShipWidget(SpaceShip spaceShip, Skin skin) {
		super("", skin);
		this.spaceShip = spaceShip;
		wasInSpace = spaceShip.isInSpace();
		getLabel().setText(getCurrentText());
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (wasInSpace != spaceShip.isInSpace()) {
			getLabel().setText(getCurrentText());
			wasInSpace = spaceShip.isInSpace();
		}
	}

	private StringBuilder getCurrentText(){
		StringBuilder stringBuilder = new StringBuilder(spaceShip.getName());
		stringBuilder.append(" (");
		if (spaceShip.isInSpace()) {
			stringBuilder.append("Space");
		} else {
			stringBuilder.append(spaceShip.getStart().getName());
		}
		stringBuilder.append(")");
		return stringBuilder;
	}
}
