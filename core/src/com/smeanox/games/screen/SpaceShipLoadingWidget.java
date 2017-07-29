package com.smeanox.games.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.smeanox.games.Consts;
import com.smeanox.games.world.ResourceType;
import com.smeanox.games.world.SpaceShip;

import java.util.EnumMap;

public class SpaceShipLoadingWidget extends Table {

	private SpaceShip spaceShip;
	private EnumMap<ResourceType, Drawable> resourceIcons;
	private EnumMap<ResourceType, TextField> resourceTextFields;

	public SpaceShipLoadingWidget(Skin skin) {
		super(skin);

		resourceIcons = new EnumMap<ResourceType, Drawable>(ResourceType.class);
		for (ResourceType resourceType : ResourceType.values()) {
			resourceIcons.put(resourceType, skin.getDrawable("resource/" + resourceType.name()));
		}

		resourceTextFields = new EnumMap<ResourceType, TextField>(ResourceType.class);

		int x = 0;
		for (ResourceType resourceType : ResourceType.values()) {
			if (x % Consts.LOADING_RESOURCE_PER_LINE == 0) {
				row();
			}
			add(new Image(resourceIcons.get(resourceType))).width(50);
			TextField textField = new TextField("0", skin);
			resourceTextFields.put(resourceType, textField);
			textField.addListener(new ResourceListener(resourceType));
			add(textField).width(70);
			x++;
		}
	}

	public SpaceShip getSpaceShip() {
		return spaceShip;
	}

	public void setSpaceShip(SpaceShip spaceShip) {
		this.spaceShip = spaceShip;
		for (ResourceType resourceType : ResourceType.values()) {
			resourceTextFields.get(resourceType).setText("" + spaceShip.getResources().get(resourceType).val);
		}
	}

	private class ResourceListener extends ChangeListener {

		private final ResourceType resourceType;

		private ResourceListener(ResourceType resourceType) {
			this.resourceType = resourceType;
		}

		@Override
		public void changed(ChangeEvent event, Actor actor) {
			String text = ((TextField) actor).getText();
			try {
				float oldValue = spaceShip.getResources().get(resourceType).val;
				float newValue = text.length() > 0 ? Float.parseFloat(text) : 0;
				if (newValue < 0 || !spaceShip.moveResource(resourceType, newValue - oldValue)){
					event.cancel();
				}
			} catch (NumberFormatException e) {
				event.cancel();
			}
		}
	}
}
