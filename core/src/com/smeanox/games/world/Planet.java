package com.smeanox.games.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.smeanox.games.Consts;
import com.smeanox.games.screen.Atlas;
import com.smeanox.games.util.Rapper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;

public class Planet {

	private long time;
	private TextureRegion texture;
	private String name;
	private final Universe universe;
	private final int width;
	private final int height;
	private final int x, y;
	private float solarMultiplier;

	private final GridElement[][] grid;
	private final List<Building> buildings;
	private final List<SpaceShip> spaceShips, arrivingSpaceShips;
	private final EnumMap<ResourceType, Rapper<Float>> resources;
	private int totalDudes, dudesCapacity;
	private int spaceShipsCapacity;
	private boolean visited;
	private long lastDudeIncrease;

	private List<PlanetListener> listeners;

	public Planet(String name, Universe universe, int width, int height, int x, int y, float solarMultiplier) {
		this.name = name;
		this.universe = universe;
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		this.solarMultiplier = solarMultiplier;
		this.grid = new GridElement[height][width];
		this.buildings = new ArrayList<Building>();
		this.spaceShips = new ArrayList<SpaceShip>();
		this.arrivingSpaceShips = new ArrayList<SpaceShip>();
		this.resources = new EnumMap<ResourceType, Rapper<Float>>(ResourceType.class);

		this.spaceShipsCapacity = Consts.SPACE_SHIP_CAPACITY_WITHOUT_PORT;

		listeners = new ArrayList<PlanetListener>();
	}

	public void generatePlanet(EnumSet<GridElementType> possibleTypes, String textureName) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				grid[y][x] = new GridElement(GridElementType.sand, Consts.START_LEVEL, x, y);
			}
		}

		for (GridElementType gridElementType : GridElementType.values()) {
			if (possibleTypes.contains(gridElementType)) {
				generateFlow(gridElementType,
						MathUtils.ceil(width * height * Consts.ELEMENT_FLOW_AMOUNT.get(gridElementType)),
						Consts.ELEMENT_FLOW_CONTINUE.get(gridElementType));
			}
		}

		for (ResourceType resourceType : ResourceType.values()) {
			resources.put(resourceType, new Rapper<Float>(0f));
		}

		texture = Atlas.textures.atlas.findRegion(textureName);
	}

	public void discoverPlanet(){
		if (visited){
			return;
		}
		setVisited(true);

		int x, y;
		do {
			x = MathUtils.random(width - 1);
			y = MathUtils.random(height - 1);
		} while (grid[y][x].getType() != GridElementType.sand || grid[y][x].getBuilding() != null);
		buildFreeBuilding(BuildingType.city, x, y);

		fireDiscovered();
	}

	private void generateFlow(GridElementType type, int count, float continueChance) {
		while (count > 0) {
			int x = MathUtils.random(width - 1);
			int y = MathUtils.random(height - 1);
			while (count > 0
					&& (grid[y][x].getType() == GridElementType.sand
					|| grid[y][x].getType() == type)
					&& MathUtils.randomBoolean(continueChance)) {
				if (grid[y][x].getType() != type) {
					grid[y][x].setType(type);
					count--;
				}
				x = MathUtils.clamp(x + MathUtils.random(-1, 1), 0, width - 1);
				y = MathUtils.clamp(y + MathUtils.random(-1, 1), 0, height - 1);
			}
		}
	}

	public boolean buildFreeBuilding(BuildingType type, int x, int y) {
		for (ResourceType resourceType : ResourceType.values()) {
			resources.get(resourceType).val += type.config.resourcesBuild.get(resourceType);
		}
		if (!Building.canBuild(type, this, x, y)) {
			for (ResourceType resourceType : ResourceType.values()) {
				resources.get(resourceType).val -= type.config.resourcesBuild.get(resourceType);
			}
			return false;
		} else {
			new Building(type).build(this, x, y);
			return true;
		}
	}

	public Universe getUniverse() {
		return universe;
	}

	public long getTime() {
		return time;
	}

	public TextureRegion getTexture() {
		return texture;
	}

	public void setTexture(TextureRegion texture) {
		this.texture = texture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		fireNameChanged();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public float getSolarMultiplier() {
		return solarMultiplier;
	}

	public GridElement[][] getGrid() {
		return grid;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public List<SpaceShip> getSpaceShips() {
		return spaceShips;
	}

	public List<SpaceShip> getArrivingSpaceShips() {
		return arrivingSpaceShips;
	}

	public EnumMap<ResourceType, Rapper<Float>> getResources() {
		return resources;
	}

	public int getDudesCapacity() {
		return dudesCapacity;
	}

	public void setDudesCapacity(int dudesCapacity) {
		this.dudesCapacity = dudesCapacity;
	}

	public void addDudesCapacity(int dudesCapacity) {
		this.dudesCapacity += dudesCapacity;
		fireDudesChanged();
	}

	public int getTotalDudes() {
		return totalDudes;
	}

	public void setTotalDudes(int totalDudes) {
		this.totalDudes = totalDudes;
		fireDudesChanged();
	}

	public void addTotalDudes(int totalDudes) {
		this.totalDudes += totalDudes;
		fireDudesChanged();
	}

	public int getSpaceShipsCapacity() {
		return spaceShipsCapacity;
	}

	public void setSpaceShipsCapacity(int spaceShipsCapacity) {
		this.spaceShipsCapacity = spaceShipsCapacity;
	}

	public void addSpaceShipsCapacity(int spaceShipsCapacity) {
		this.spaceShipsCapacity += spaceShipsCapacity;
	}

	public int getFreeSpaceShipCapacity(){
		return spaceShipsCapacity - spaceShips.size() - arrivingSpaceShips.size();
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public void step() {
		time += Consts.UNIVERSE_STEP_SIZE;

		for (Building building : buildings) {
			building.step(this);
		}

		if (time - lastDudeIncrease > Consts.DUDE_INCREASE_TIME) {
			lastDudeIncrease = time;
			int increase = MathUtils.ceil(totalDudes * 0.01f);
			if (totalDudes + increase <= dudesCapacity) {
				resources.get(ResourceType.dudes).val += increase;
				addTotalDudes(increase);
			}
		}
	}

	public void addListener(PlanetListener listener) {
		listeners.add(listener);
	}

	public void removeListener(PlanetListener listener) {
		listeners.remove(listener);
	}

	public void fireSpaceShipsChanged(SpaceShip spaceShip) {
		for (PlanetListener listener : listeners) {
			listener.spaceShipArrived(this, spaceShip);
		}
	}

	public void fireNameChanged() {
		for (PlanetListener listener : listeners) {
			listener.planetNameChanged(this);
		}
	}

	public void fireDudesChanged() {
		for (PlanetListener listener : listeners) {
			listener.dudesChanged(this);
		}
	}

	public void fireDiscovered() {
		for (PlanetListener listener : listeners) {
			listener.discovered(this);
		}
	}

	public static abstract class PlanetListener {
		public void spaceShipArrived(Planet planet, SpaceShip spaceShip) {
		}

		public void planetNameChanged(Planet planet) {
		}

		public void dudesChanged(Planet planet) {
		}

		public void discovered(Planet planet){
		}
	}
}
