package com.smeanox.games.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.smeanox.games.Consts;
import com.smeanox.games.screen.Atlas;
import com.smeanox.games.util.Rapper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class Planet {

	private long time;
	private TextureRegion texture;
	private String name;
	private final int width;
	private final int height;
	private final int x, y;
	private float solarMultiplier;

	private final GridElement[][] grid;
	private final List<Building> buildings;
	private final List<SpaceShip> spaceShips;
	private final EnumMap<ResourceType, Rapper<Float>> resources;
	private int totalDudes, dudesCapacity;
	private int spaceShipsCapacity;
	private boolean visited;

	public Planet(String name, int width, int height, int x, int y, float solarMultiplier) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		this.solarMultiplier = solarMultiplier;
		this.grid = new GridElement[height][width];
		this.buildings = new ArrayList<Building>();
		this.spaceShips = new ArrayList<SpaceShip>();
		this.resources = new EnumMap<ResourceType, Rapper<Float>>(ResourceType.class);

		generatePlanet();
	}

	private void generatePlanet(){
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				this.grid[y][x] = new GridElement(GridElementType.sand, 0, x, y);
			}
		}

		for (ResourceType resourceType : ResourceType.values()) {
			resources.put(resourceType, new Rapper<Float>(0f));
		}

		texture = Atlas.textures.atlas.findRegion("space/planet");
	}

	public long getTime() {
		return time;
	}

	public TextureRegion getTexture() {
		return texture;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	}

	public int getTotalDudes() {
		return totalDudes;
	}

	public void setTotalDudes(int totalDudes) {
		this.totalDudes = totalDudes;
	}

	public void addTotalDudes(int totalDudes) {
		this.totalDudes += totalDudes;
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
	}
}
