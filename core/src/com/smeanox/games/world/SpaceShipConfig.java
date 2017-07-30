package com.smeanox.games.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.EnumMap;

public class SpaceShipConfig {

	public final TextureRegion texture, smallTexture;
	public final String name;
	public final float capacity;
	public final float propellantPerWeightAndDistance;
	public final float speed;
	public final float weight;
	public final EnumMap<ResourceType, Float> resourcesBuild;
	public final EnumMap<ResourceType, Float> resourcesDestroy;

	public SpaceShipConfig(TextureRegion texture, TextureRegion smallTexture, String name,
						   float capacity, float propellantPerWeightAndDistance,
						   float speed, float weight,
						   EnumMap<ResourceType, Float> resourcesBuild,
						   EnumMap<ResourceType, Float> resourcesDestroy) {
		this.texture = texture;
		this.smallTexture = smallTexture;
		this.name = name;
		this.capacity = capacity;
		this.propellantPerWeightAndDistance = propellantPerWeightAndDistance;
		this.speed = speed;
		this.weight = weight;
		this.resourcesBuild = resourcesBuild;
		this.resourcesDestroy = resourcesDestroy;
	}
}
