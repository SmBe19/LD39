package com.smeanox.games.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.StringBuilder;
import com.smeanox.games.world.SpaceShip;

public class ShipWidget extends TextButton {

	private final SpaceShip spaceShip;

	public ShipWidget(SpaceShip spaceShip, Skin skin) {
		super("", skin);
		this.spaceShip = spaceShip;
		getLabel().setText(getCurrentText());

		spaceShip.addListener(new SpaceShip.SpaceShipListener() {
			@Override
			public void spaceShipStateChanged(SpaceShip spaceShip) {
				getLabel().setText(getCurrentText());
			}

			@Override
			public void spaceShipNameChanged(SpaceShip spaceShip) {
				getLabel().setText(getCurrentText());
			}
		});
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
