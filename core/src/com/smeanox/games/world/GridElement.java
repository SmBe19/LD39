package com.smeanox.games.world;

public class GridElement {
	private GridElemenType type;
	private Building building;
	private float level;
	private int x, y;

	public GridElement(GridElemenType type, float level, int x, int y) {
		this.type = type;
		this.level = level;
		this.x = x;
		this.y = y;
	}

	public GridElemenType getType() {
		return type;
	}

	public void setType(GridElemenType type) {
		this.type = type;
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
	}

	public float getLevel() {
		return level;
	}

	public void setLevel(float level) {
		this.level = level;
	}

	public void addLevel(float level) {
		this.level += level;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
