package com.smeanox.games.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.smeanox.games.Consts;
import com.smeanox.games.world.Building;
import com.smeanox.games.world.BuildingType;
import com.smeanox.games.world.GridElement;
import com.smeanox.games.world.Planet;

public class BuildWidget extends Widget {

	private Planet planet;
	private float prefWidth, prefHeight;
	private BuildingType currentBuildingType;
	private boolean currentDestroy;
	private float mouseX, mouseY;
	private int mouseXX, mouseYY;

	public BuildWidget() {
		prefWidth = 0;
		prefHeight = 0;
		currentBuildingType = null;
		currentDestroy = false;

		addListener(new InputListener(){
			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				setxy(x, y);
				return true;
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setxy(x, y);
				if (currentDestroy){
					GridElement gridElement = planet.getGrid()[mouseYY][mouseXX];
					if (gridElement.getBuilding() != null && gridElement.getBuilding().canDestroy(planet)) {
						gridElement.getBuilding().destroy(planet);
					}
					return true;
				} else if (currentBuildingType != null){
					new Building(currentBuildingType).build(planet, mouseXX, mouseYY);
					return true;
				}

				return super.touchDown(event, x, y, pointer, button);
			}

			private void setxy(float x, float y) {
				mouseX = x;
				mouseY = y;
				mouseXX = (int) (mouseX / Consts.GRID_WIDTH);
				mouseYY = (int) (mouseY / Consts.GRID_HEIGHT);
			}
		});
	}

	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
		prefWidth = Consts.GRID_WIDTH * planet.getWidth();
		prefHeight = Consts.GRID_HEIGHT * planet.getHeight();
	}

	@Override
	public float getPrefWidth() {
		return prefWidth;
	}

	@Override
	public float getPrefHeight() {
		return prefHeight;
	}

	public BuildingType getCurrentBuildingType() {
		return currentBuildingType;
	}

	public void setCurrentBuildingType(BuildingType currentBuildingType) {
		this.currentBuildingType = currentBuildingType;
		if (currentBuildingType != null) {
			currentDestroy = false;
		}
	}

	public boolean isCurrentDestroy() {
		return currentDestroy;
	}

	public void setCurrentDestroy(boolean currentDestroy) {
		this.currentDestroy = currentDestroy;
		if (currentDestroy) {
			currentBuildingType = null;
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		batch.setColor(1, 1, 1, parentAlpha);
		float ax = getX();
		float ay = getY();

		GridElement[][] grid = planet.getGrid();
		int height = planet.getHeight();
		int width = planet.getWidth();
		for(int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				GridElement gridElement = grid[y][x];
				batch.draw(gridElement.getType().texture,
						ax + x * Consts.GRID_WIDTH,
						ay + y * Consts.GRID_HEIGHT,
						Consts.GRID_WIDTH, Consts.GRID_HEIGHT);
				if (gridElement.getBuilding() != null) {
					if (currentDestroy && x == mouseXX && y == mouseYY){
						if (gridElement.getBuilding().canDestroy(planet)){
							batch.setColor(1, 0.5f, 0.5f, 0.5f);
						} else {
							batch.setColor(1, 1, 1, 0.5f);
						}
					}

					batch.draw(gridElement.getBuilding().getType().config.texture,
							ax + x * Consts.GRID_WIDTH,
							ay + y * Consts.GRID_HEIGHT,
							Consts.GRID_WIDTH, Consts.GRID_HEIGHT);

					if (currentDestroy && x == mouseXX && y == mouseYY){
						batch.setColor(1, 1, 1, 1);
					}
				}
			}
		}

		if (currentBuildingType != null) {
			if (Building.canBuild(currentBuildingType, planet, mouseXX, mouseYY)) {
				batch.setColor(1, 1, 1, 0.5f);
			} else {
				batch.setColor(1, 0.5f, 0.5f, 0.5f);
			}
			batch.draw(currentBuildingType.config.texture,
					ax + mouseXX * Consts.GRID_WIDTH,
					ay + mouseYY * Consts.GRID_HEIGHT,
					Consts.GRID_WIDTH, Consts.GRID_HEIGHT);
		}
	}
}
