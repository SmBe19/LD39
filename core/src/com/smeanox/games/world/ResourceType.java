package com.smeanox.games.world;

public enum ResourceType {
	electricity(1),
	metal(1),
	propellant(1),
	water(1),
	dudes(1),
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
