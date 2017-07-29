package com.smeanox.games.world;

import com.smeanox.games.Consts;

import java.util.ArrayList;
import java.util.List;

public class Universe {

	private List<Planet> planets;
	private List<SpaceShip> spaceShips;
	private long time;

	public Universe(){
		planets = new ArrayList<Planet>();
		spaceShips = new ArrayList<SpaceShip>();
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	public List<SpaceShip> getSpaceShips() {
		return spaceShips;
	}

	public long getTime() {
		return time;
	}

	public void step() {
		time += Consts.UNIVERSE_STEP_SIZE;

		for (Planet planet : planets) {
			planet.step();
		}
		for (SpaceShip spaceShip : spaceShips) {
			spaceShip.step(this);
		}
	}

	public void bigBang() {
		// TODO create universe
		for(int i = 0; i < 10; i++) {
			Planet planet = new Planet("Planet " + i, 10, 10, i * Consts.PLANET_MIN_DIST, i * 514 % 123, 1);
			planets.add(planet);
		}
	}
}
