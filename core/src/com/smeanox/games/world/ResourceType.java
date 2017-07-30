package com.smeanox.games.world;

public enum ResourceType {
	electricity(0.1f),
	metal(1),
	propellant(0.1f),
	dudes(2),
	water(1),
	solarpanel(1),
	oil(1),
	gas(1),
	coal(1),
	;

	public final float weight;

	ResourceType(float weight) {
		this.weight = weight;
	}
}
