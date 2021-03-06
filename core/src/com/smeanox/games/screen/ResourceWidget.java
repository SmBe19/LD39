package com.smeanox.games.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.smeanox.games.Consts;
import com.smeanox.games.world.Planet;
import com.smeanox.games.world.ResourceType;

import java.util.EnumMap;

public class ResourceWidget extends Widget {

	private final Skin skin;
	private final BitmapFont font;
	private Planet planet;
	private float prefWidth, prefHeight;
	private final Drawable resourceBackground;
	private EnumMap<ResourceType, Drawable> resourceIcons;
	private boolean horizontal;
	private final float padding = 5;

	public ResourceWidget(Skin skin, boolean horizontal) {
		this.skin = skin;
		this.horizontal = horizontal;
		if (horizontal) {
			prefWidth = Consts.RESOURCE_WIDTH * ResourceType.values().length + padding * 2;
			prefHeight = Consts.RESOURCE_HEIGHT;
		} else {
			prefWidth = Consts.RESOURCE_WIDTH;
			prefHeight = Consts.RESOURCE_HEIGHT * ResourceType.values().length + padding * 2;
		}

		font = skin.getFont("font-arial12");
		resourceBackground = skin.getDrawable("resource/background");
		resourceIcons = new EnumMap<ResourceType, Drawable>(ResourceType.class);
		for (ResourceType resourceType : ResourceType.values()) {
			resourceIcons.put(resourceType, skin.getDrawable("resource/" + resourceType.name()));
		}
	}

	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
		setVisible(planet != null);
	}

	@Override
	public float getPrefWidth() {
		return prefWidth;
	}

	@Override
	public float getPrefHeight() {
		return prefHeight;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		if (planet == null) {
			return;
		}

		float ax = getX();
		float ay = getY();
		float awidth = getWidth();
		float aheight = getHeight();

		batch.setColor(1, 1, 1, 1);

		resourceBackground.draw(batch, ax, ay, awidth, aheight);

		float capHeight = font.getCapHeight();
		float x = 0;
		float y = horizontal ? 0 : (aheight - Consts.RESOURCE_HEIGHT - 2 * padding);
		float iconsize = Consts.RESOURCE_HEIGHT * Consts.RESOURCE_ICON_SIZE;
		float iconx = ax + padding + Consts.RESOURCE_HEIGHT * Consts.RESOURCE_ICON_PADDING;
		float icony = ay + (horizontal ? 0 : padding) + ((horizontal ? aheight : Consts.RESOURCE_HEIGHT) - iconsize) * 0.5f;
		float fontx = ax + padding + iconsize + Consts.RESOURCE_HEIGHT * Consts.RESOURCE_ICON_PADDING * 2;
		float fonty = ay + (horizontal ? Consts.RESOURCE_HEIGHT * 0.3f : padding) + ((horizontal ? aheight : Consts.RESOURCE_HEIGHT) + capHeight) * 0.5f;
		float fontChangex = ax + padding + (horizontal ? iconsize + Consts.RESOURCE_HEIGHT * Consts.RESOURCE_ICON_PADDING * 2 : Consts.RESOURCE_WIDTH);
		float fontChangey = ay + (horizontal ? -Consts.RESOURCE_HEIGHT * 0.3f : padding) + ((horizontal ? aheight : Consts.RESOURCE_HEIGHT) + capHeight) * 0.5f;
		for (ResourceType resourceType : ResourceType.values()) {
			int val = MathUtils.floor(planet.getResources().get(resourceType).val);
			int changeVal = MathUtils.ceil(planet.getResourceChange().get(resourceType).val / Consts.UNIVERSE_TIME_MULTIPLIER);
			resourceIcons.get(resourceType).draw(batch, x + iconx, y + icony, iconsize, iconsize);
			font.draw(batch, "" + val, x + fontx, y + fonty);
			font.draw(batch, (changeVal >= 0 ? "+" : "") + changeVal, x + fontChangex, y + fontChangey);
			if (horizontal) {
				x += Consts.RESOURCE_WIDTH;
			} else {
				y -= Consts.RESOURCE_HEIGHT;
			}
		}
	}
}
