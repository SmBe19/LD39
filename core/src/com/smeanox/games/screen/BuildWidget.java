package com.smeanox.games.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
	private ResourceInfoWidget resourceInfoWidget;

	private long lastTouchDown;

	// TODO show level

	public BuildWidget() {
		prefWidth = 0;
		prefHeight = 0;
		currentBuildingType = null;
		currentDestroy = false;

		addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent event, float x, float y) {
				setxy(x, y);
				if (currentDestroy){
					GridElement gridElement = planet.getGrid()[mouseYY][mouseXX];
					if (gridElement.getBuilding() != null && gridElement.getBuilding().canDestroy(planet)) {
						gridElement.getBuilding().destroy(planet);
					}
				} else if (currentBuildingType != null){
					new Building(currentBuildingType).build(planet, mouseXX, mouseYY);
				} else {
					long now = planet.getTime();
					if (now - lastTouchDown < Consts.DOUBLE_CLICK_TIME){
						lastTouchDown = 0;
						GridElement gridElement = planet.getGrid()[mouseYY][mouseXX];
						if (gridElement.getBuilding() != null) {
							gridElement.getBuilding().toggle(planet);
						}
					} else {
						lastTouchDown = now;
					}
				}
			}

			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				setxy(x, y);
				return super.mouseMoved(event, x, y);
			}

			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if(keycode == Input.Keys.ESCAPE){
					cancelAction();
					return true;
				}
				return false;
			}

			private void setxy(float x, float y) {
				mouseX = x;
				mouseY = y;
				mouseXX = (int) (mouseX / Consts.GRID_WIDTH);
				mouseYY = (int) (mouseY / Consts.GRID_HEIGHT);
			}
		});

		addListener(new ClickListener(Input.Buttons.RIGHT){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				cancelAction();
			}
		});
	}

	private void cancelAction() {
		setCurrentDestroy(false);
		setCurrentBuildingType(null);
		getStage().setKeyboardFocus(null);
	}

	public Planet getPlanet() {
		return planet;
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
		prefWidth = Consts.GRID_WIDTH * planet.getWidth();
		prefHeight = Consts.GRID_HEIGHT * planet.getHeight();
		invalidateHierarchy();
	}

	public ResourceInfoWidget getResourceInfoWidget() {
		return resourceInfoWidget;
	}

	public void setResourceInfoWidget(ResourceInfoWidget resourceInfoWidget) {
		this.resourceInfoWidget = resourceInfoWidget;
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
		if (resourceInfoWidget != null) {
			resourceInfoWidget.setBuildingType(currentBuildingType);
		}
	}

	public boolean isCurrentDestroy() {
		return currentDestroy;
	}

	public void setCurrentDestroy(boolean currentDestroy) {
		this.currentDestroy = currentDestroy;
		if (currentDestroy) {
			currentBuildingType = null;
			if (resourceInfoWidget != null) {
				resourceInfoWidget.setBuildingType(null);
			}
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
				Building building = gridElement.getBuilding();
				if (building != null) {
					if (currentDestroy && x == mouseXX && y == mouseYY){
						if (building.canDestroy(planet)){
							batch.setColor(1, 0.5f, 0.5f, 0.5f);
						} else {
							batch.setColor(1, 1, 1, 0.5f);
						}
					} else if (!building.isActive()){
						batch.setColor(1f, 0.3f, 0.3f, 0.5f);
					}

					batch.draw(building.getType().config.texture,
							ax + x * Consts.GRID_WIDTH,
							ay + y * Consts.GRID_HEIGHT,
							Consts.GRID_WIDTH, Consts.GRID_HEIGHT);

					batch.setColor(1, 1, 1, 1);
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
