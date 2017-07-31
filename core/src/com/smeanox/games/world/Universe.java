package com.smeanox.games.world;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.StringBuilder;
import com.smeanox.games.Consts;
import com.smeanox.games.util.GridElementSetBuilder;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Universe {

	private List<Planet> planets;
	private List<SpaceShip> spaceShips;
	private long time;
	private Planet earth;
	private Planet alphaCentauri;

	public Universe() {
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

	public Planet getEarth() {
		return earth;
	}

	public Planet getAlphaCentauri() {
		return alphaCentauri;
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

	public int getTotalDudes(){
		int sum = 0;
		for (Planet planet : planets) {
			sum += planet.getTotalDudes();
		}
		return sum;
	}

	public boolean isWon(){
		return alphaCentauri.getTotalDudes() == getTotalDudes();
	}

	public boolean isLost(){
		return getTotalDudes() == 0;
	}

	private boolean possiblePosition(int x, int y) {
		for (Planet planet : planets) {
			if ((planet.getX() - x) * (planet.getX() - x) + (planet.getY() - y) * (planet.getY() - y) < Consts.PLANET_MIN_DIST * Consts.PLANET_MIN_DIST) {
				return false;
			}
		}
		return true;
	}

	public void bigBang() {
		planets.clear();
		spaceShips.clear();

		initEarth();
		initAlphaCentauri();

		List<EnumSet<GridElementType>> elements = new ArrayList<EnumSet<GridElementType>>();
		for (int i = 0; i < Consts.PLANET_COUNT; i++) {
			elements.add(EnumSet.noneOf(GridElementType.class));
		}

		for (GridElementType gridElementType : GridElementType.values()) {
			Set<Integer> choices = new HashSet<Integer>();
			for (int i = 0; i < Consts.PLANET_ELEMENTS_COUNT.get(gridElementType); i++) {
				int x;
				do {
					x = MathUtils.random(Consts.PLANET_COUNT - 1);
				} while (choices.contains(x));
				elements.get(x).add(gridElementType);
				choices.add(x);
			}
		}

		for (int i = 0; i < Consts.PLANET_COUNT; i++) {
			int x = 0, y = 0;
			while (!possiblePosition(x, y)) {
				x = MathUtils.random(0, Consts.UNIVERSE_SIZE);
				y = MathUtils.random(0, Consts.UNIVERSE_SIZE);
			}
			boolean small = MathUtils.randomBoolean(Consts.PLANET_SMALL_CHANCE);
			int width = MathUtils.random(Consts.GRID_MIN_SIZE, small ? ((int) (Consts.GRID_MAX_SIZE * Consts.PLANET_SMALL_MULTIPLIER)) : Consts.GRID_MAX_SIZE);
			int height = MathUtils.random(Consts.GRID_MIN_SIZE, small ? ((int) (Consts.GRID_MAX_SIZE * Consts.PLANET_SMALL_MULTIPLIER)) : Consts.GRID_MAX_SIZE);
			float solarMultiplier = MathUtils.random(Consts.SOLAR_MULTIPLIER_MIN, Consts.SOLAR_MULTIPLIER_MAX);
			Planet planet = new Planet(getRandomName(8), this, width, height, x, y, solarMultiplier);
			planet.generatePlanet(elements.get(i), "space/planet" + (MathUtils.random(6)));
			planets.add(planet);
		}
	}

	public static String getRandomName(int length){
		StringBuilder stringBuilder = new StringBuilder();
		String vocals = "aeiou";
		String consonants = "bcdfghjklmnpqrstvwxyz";
		for(int i = 0; i < length; i++) {
			if (i % 2 == 0){
				stringBuilder.append(vocals.charAt(MathUtils.random(vocals.length() - 1)));
			} else {
				stringBuilder.append(consonants.charAt(MathUtils.random(consonants.length() - 1)));
			}
		}
		return stringBuilder.toString();
	}

	private void initEarth() {
		earth = new Planet("Earth", this, 10, 10, 0, Consts.UNIVERSE_SIZE, 0);
		earth.generatePlanet(new GridElementSetBuilder()
						.add(GridElementType.coal)
						.add(GridElementType.oil)
						.add(GridElementType.gas)
						.add(GridElementType.metal)
						.add(GridElementType.water).build(),
				"space/earth");
		earth.setVisited(true);
		earth.setTotalDudes(1000);
		for (ResourceType resourceType : ResourceType.values()) {
			earth.getResources().get(resourceType).val = Consts.RESOURCE_START.get(resourceType);
		}
		earth.getResources().get(ResourceType.dudes).val = (float) Consts.DUDES_START_COUNT;
		int cityCount = MathUtils.ceil((float) Consts.DUDES_START_COUNT / BuildingType.city.config.dudesCapacityIncrease);
		int width = MathUtils.floor((float) Math.sqrt(cityCount));
		int y = 0;
		int x = 0;
		for (int i = 0; i < cityCount; i++) {
			if (!earth.buildFreeBuilding(BuildingType.city, x, y)) {
				i--;
			}
			x++;
			if (x % width == 0) {
				x = 0;
				y++;
			}
		}
		planets.add(earth);
	}

	private void initAlphaCentauri() {
		alphaCentauri = new Planet("Unadexus", this, Consts.GRID_MAX_SIZE, Consts.GRID_MAX_SIZE, Consts.UNIVERSE_SIZE, 0, 1000);
		alphaCentauri.generatePlanet(new GridElementSetBuilder()
						.add(GridElementType.coal)
						.add(GridElementType.oil)
						.add(GridElementType.gas)
						.add(GridElementType.metal)
						.add(GridElementType.water).build(),
				"space/alphacentauri");
		planets.add(alphaCentauri);
	}
}
