package com.smeanox.games.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpaceShipConfig {

	public final TextureRegion texture;
	public final float capacity;
	public final float propellantPerWeightAndDistance;
	public final float speed;
	public final float buildTime;

	public SpaceShipConfig(TextureRegion texture, float capacity,
						   float propellantPerWeightAndDistance,
						   float speed, float buildTime) {
		this.texture = texture;
		this.capacity = capacity;
		this.propellantPerWeightAndDistance = propellantPerWeightAndDistance;
		this.speed = speed;
		this.buildTime = buildTime;
	}
}
