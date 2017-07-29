package com.smeanox.games.world;

import com.smeanox.games.Consts;

import java.util.List;

public class Universe {

	private List<Planet> planets;
	private List<SpaceShip> spaceShips;
	private long time;

	public void step() {
		time += Consts.UNIVERSE_STEP_SIZE;

		for (Planet planet : planets) {
			planet.step();
		}
		for (SpaceShip spaceShip : spaceShips) {
			spaceShip.step(this);
		}
	}
}
