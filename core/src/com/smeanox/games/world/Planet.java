package com.smeanox.games.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.smeanox.games.Consts;
import com.smeanox.games.util.Rapper;

import java.util.EnumMap;
import java.util.List;

public class Planet {

	private long time;
	private TextureRegion texture;
	private final int width;
	private final int height;
	private final int x, y;
	private float solarMultiplier;

	private GridElement[][] grid;
	private List<Building> buildings;
	private List<SpaceShip> spaceShips;
	private EnumMap<ResourceType, Rapper<Float>> resources;
	private int totalDudes, dudesCapacity;
	private int spaceShipsCapacity;
	private boolean visited;

	public Planet(int width, int height, int x, int y) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}

	public long getTime() {
		return time;
	}

	public TextureRegion getTexture() {
		return texture;
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

	public void step() {
		time += Consts.UNIVERSE_STEP_SIZE;

		for (Building building : buildings) {
			building.step(this);
		}
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
}
