package com.smeanox.games.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.smeanox.games.Consts;
import com.smeanox.games.world.BuildingType;
import com.smeanox.games.world.Planet;
import com.smeanox.games.world.ResourceType;
import com.smeanox.games.world.SpaceShipType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class ResourceInfoWidget extends Widget {

	private final Skin skin;
	private final BitmapFont font;
	private BuildingType buildingType;
	private SpaceShipType spaceShipType;
	private Planet planet;
	private float prefWidth, prefHeight;
	private final Drawable resourceBackground;
	private EnumMap<ResourceType, Drawable> resourceIcons;
	private final float padding = 10;
	private final float spacing = 10;
	private final float fontoff;
	private final List<Actor> actorsToHide;

	public ResourceInfoWidget(Skin skin) {
		this.skin = skin;
		calculateSize();

		actorsToHide = new ArrayList<Actor>();

		font = skin.getFont("font-arial12");
		fontoff = font.getCapHeight() * 1.1f;
		resourceBackground = skin.getDrawable("resource/background");
		resourceIcons = new EnumMap<ResourceType, Drawable>(ResourceType.class);
		for (ResourceType resourceType : ResourceType.values()) {
			resourceIcons.put(resourceType, skin.getDrawable("resource/" + resourceType.name()));
		}
	}

	public BuildingType getBuildingType() {
		return buildingType;
	}

	public void setBuildingType(BuildingType buildingType) {
		this.buildingType = buildingType;
		setVisible(buildingType != null || spaceShipType != null);
		setOthersVisible(buildingType == null);
		calculateSize();
	}

	public SpaceShipType getSpaceShipType() {
		return spaceShipType;
	}

	public void setSpaceShipType(SpaceShipType spaceShipType) {
		this.spaceShipType = spaceShipType;
		setVisible(buildingType != null || spaceShipType != null);
		setOthersVisible(buildingType == null);
		calculateSize();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
	}

	private void setOthersVisible(boolean visible) {
		for (Actor actor : actorsToHide) {
			actor.setVisible(visible);
		}
		invalidateHierarchy();
	}

	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
	}

	public List<Actor> getActorsToHide() {
		return actorsToHide;
	}

	private void calculateSize (){
		if (buildingType == null){
			if (spaceShipType == null){
				prefWidth = 0;
				prefHeight = 0;
			} else {
				prefWidth = Consts.RESOURCE_WIDTH * 2 + padding * 2;
				prefHeight = 2 * fontoff + 2 * padding + 1 * spacing;
				int[] cnt = new int[2];
				for (ResourceType resourceType : ResourceType.values()) {
					if (spaceShipType.config.resourcesBuild.get(resourceType) != 0){
						cnt[0]++;
					}
					if (spaceShipType.config.resourcesDestroy.get(resourceType) != 0){
						cnt[1]++;
					}
				}
				for (int i = 0; i < 2; i++) {
					prefHeight += cnt[i] * Consts.RESOURCE_HEIGHT;
				}
			}
		} else {
			prefWidth = Consts.RESOURCE_WIDTH * 2 + padding * 2;
			prefHeight = 3 * fontoff + 2 * padding + 2 * spacing;
			int[] cnt = new int[3];
			for (ResourceType resourceType : ResourceType.values()) {
				if (buildingType.config.resourcesUsage.get(resourceType) != 0){
					cnt[0]++;
				}
				if (buildingType.config.resourcesBuild.get(resourceType) != 0){
					cnt[1]++;
				}
				if (buildingType.config.resourcesDestroy.get(resourceType) != 0){
					cnt[2]++;
				}
			}
			if (buildingType.config.dudesNeeded != 0){
				cnt[0]++;
			}
			for (int i = 0; i < 3; i++) {
				prefHeight += MathUtils.ceil(cnt[i] / 2.f) * Consts.RESOURCE_HEIGHT;
			}
		}
		invalidateHierarchy();
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

		if (buildingType == null && spaceShipType == null) {
			return;
		}

		float ax = getX();
		float ay = getY();
		float awidth = getWidth();
		float aheight = getHeight();

		batch.setColor(1, 1, 1, 1);

		resourceBackground.draw(batch, ax, ay, awidth, aheight);

		float yy = ay + aheight - padding;

		EnumMap<ResourceType, Float> resourcesUsage = null;
		EnumMap<ResourceType, Float> resourcesBuild = null;
		EnumMap<ResourceType, Float> resourcesDestroy = null;
		if (buildingType != null) {
			resourcesUsage = buildingType.config.resourcesUsage;
			resourcesBuild = buildingType.config.resourcesBuild;
			resourcesDestroy = buildingType.config.resourcesDestroy;
		} else {
			resourcesBuild = spaceShipType.config.resourcesBuild;
			resourcesDestroy = spaceShipType.config.resourcesDestroy;
		}

		if (resourcesUsage != null) {
			yy = drawList(batch, "Production", resourcesUsage, buildingType.config.dudesNeeded, planet != null ? planet.getSolarMultiplier() : 1, true, true, ax + padding, yy) - spacing;
		}
		if (resourcesBuild != null) {
			yy = drawList(batch, "Build", resourcesBuild, 0, 1, true, false, ax + padding, yy) - spacing;
		}
		if (resourcesDestroy != null) {
			yy = drawList(batch, "Destroy", resourcesDestroy, 0, 1, true, false, ax + padding, yy) - spacing;
		}
	}

	private float drawList(Batch batch, String title, EnumMap<ResourceType, Float> resources, int dudes, float solarMultiplier, boolean flipValues, boolean convert, float x, float y) {
		float ax = x;
		float ay = y;
		float iconsize = Consts.RESOURCE_HEIGHT * Consts.RESOURCE_ICON_SIZE;
		float iconOffX = Consts.RESOURCE_HEIGHT * Consts.RESOURCE_ICON_PADDING;
		float iconOffY = -(Consts.RESOURCE_HEIGHT + iconsize) * 0.5f;
		float fontOffX = iconsize + Consts.RESOURCE_HEIGHT * Consts.RESOURCE_ICON_PADDING * 2;
		float fontOffY = -(Consts.RESOURCE_HEIGHT - font.getCapHeight()) * 0.5f;
		font.draw(batch, title, ax, ay);
		ay -= fontoff;
		for (ResourceType resourceType : ResourceType.values()) {
			float usageUnrounded = resources.get(resourceType);
			if (buildingType == BuildingType.solarplant && resourceType == ResourceType.electricity){
				usageUnrounded *= solarMultiplier;
			}
			if (convert){
				usageUnrounded /= Consts.UNIVERSE_TIME_MULTIPLIER;
			}
			int usage = MathUtils.ceil(usageUnrounded);
			if (flipValues){
				usage *= -1;
			}
			if (usage != 0) {
				resourceIcons.get(resourceType).draw(batch, ax + iconOffX, ay + iconOffY, iconsize, iconsize);
				font.draw(batch, "" + usage, ax + fontOffX, ay + fontOffY);
				if (ax == x && buildingType != null) {
					ax += Consts.RESOURCE_WIDTH;
				} else {
					ax = x;
					ay -= Consts.RESOURCE_HEIGHT;
				}
			}
		}
		if (dudes != 0){
			resourceIcons.get(ResourceType.dudes).draw(batch, ax + iconOffX, ay + iconOffY, iconsize, iconsize);
			font.draw(batch, "" + dudes, ax + fontOffX, ay + fontOffY);
			if (ax == x && buildingType != null) {
				ax += Consts.RESOURCE_WIDTH;
			} else {
				ax = x;
				ay -= Consts.RESOURCE_HEIGHT;
			}
		}
		if (ax != x) {
			ay -= Consts.RESOURCE_HEIGHT;
		}
		return ay;
	}
}
