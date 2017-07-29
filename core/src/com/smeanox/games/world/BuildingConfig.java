package com.smeanox.games.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.EnumMap;
import java.util.EnumSet;

public class BuildingConfig {

	public final TextureRegion texture;
	public final int width, height;
	public final int dudesNeeded, dudesCapacityIncrease;
	public final int spaceShipsCapacityIncrease;
	public final float levelUsage;
	public final EnumSet<GridElemenType> needGridElementType;
	public final EnumMap<ResourceType, Float> resourcesBuild;
	public final EnumMap<ResourceType, Float> resourcesDestroy;
	public final EnumMap<ResourceType, Float> resourcesUsage;

	public BuildingConfig(TextureRegion texture, int width, int height, int dudesNeeded,
						  int dudesCapacityIncrease, int spaceShipsCapacityIncrease,
						  float levelUsage,
						  EnumSet<GridElemenType> needGridElementType,
						  EnumMap<ResourceType, Float> resourcesBuild,
						  EnumMap<ResourceType, Float> resourcesDestroy,
						  EnumMap<ResourceType, Float> resourcesUsage) {
		this.texture = texture;
		this.width = width;
		this.height = height;
		this.dudesNeeded = dudesNeeded;
		this.dudesCapacityIncrease = dudesCapacityIncrease;
		this.spaceShipsCapacityIncrease = spaceShipsCapacityIncrease;
		this.levelUsage = levelUsage;
		this.needGridElementType = needGridElementType;
		this.resourcesBuild = resourcesBuild;
		this.resourcesDestroy = resourcesDestroy;
		this.resourcesUsage = resourcesUsage;
	}
}
