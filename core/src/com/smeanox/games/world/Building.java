package com.smeanox.games.world;

import com.badlogic.gdx.math.MathUtils;
import com.smeanox.games.util.ErrorCatcher;

public class Building {

	private final BuildingType type;
	private int x, y;
	private boolean active;

	public Building(BuildingType type) {
		this.type = type;
		this.x = -1;
		this.y = -1;
		this.active = false;
	}

	public BuildingType getType() {
		return type;
	}

	public static boolean canBuild(BuildingType type, Planet planet, int x, int y) {
		if (!type.config.needGridElementType.contains(planet.getGrid()[y][x].getType())) {
			ErrorCatcher.get().setBuilding("Can't build on this type of ground.");
			return false;
		}
		if (planet.getGrid()[y][x].getBuilding() != null){
			ErrorCatcher.get().setBuilding("There is already a building.");
			return false;
		}
		for (ResourceType resourceType : ResourceType.values()) {
			if (planet.getResources().get(resourceType).val < type.config.resourcesBuild.get(resourceType)){
				ErrorCatcher.get().setBuilding("Not enough " + resourceType + ".");
				return false;
			}
		}
		ErrorCatcher.get().setBuilding("");
		return true;
	}

	public boolean canBuild(Planet planet, int x, int y) {
		return Building.canBuild(type, planet, x, y);
	}

	public void build(Planet planet, int x, int y) {
		if (!canBuild(planet, x, y)) {
			return;
		}
		this.x = x;
		this.y = y;
		planet.getGrid()[y][x].setBuilding(this);
		planet.getBuildings().add(this);
		for (ResourceType resourceType : ResourceType.values()) {
			planet.getResources().get(resourceType).val -= type.config.resourcesBuild.get(resourceType);
		}
		if (canActivate(planet)){
			activate(planet);
		}
	}

	public boolean canStep(Planet planet) {
		if (!active){
			return true;
		}
		for (ResourceType resourceType : ResourceType.values()) {
			if (planet.getResources().get(resourceType).val < type.config.resourcesUsage.get(resourceType)){
				return false;
			}
		}
		if (planet.getGrid()[y][x].getLevel() < type.config.levelUsage) {
			return false;
		}
		return true;
	}

	public void step(Planet planet){
		if (!active){
			return;
		}
		if (!canStep(planet)) {
			deactivate(planet, true);
			return;
		}
		for (ResourceType resourceType : ResourceType.values()) {
			planet.getResources().get(resourceType).val -= type.config.resourcesUsage.get(resourceType);
		}
		if (type == BuildingType.solarplant){
			planet.getResources().get(ResourceType.electricity).val -= (planet.getSolarMultiplier() - 1) * type.config.resourcesUsage.get(ResourceType.electricity);
		}
		planet.getGrid()[y][x].addLevel(-type.config.levelUsage);
	}

	public boolean canDestroy(Planet planet) {
		if (!canDeactivate(planet)){
			return false;
		}
		for (ResourceType resourceType : ResourceType.values()) {
			if (planet.getResources().get(resourceType).val < type.config.resourcesDestroy.get(resourceType)){
				ErrorCatcher.get().setBuilding("Not enough " + resourceType + ".");
				return false;
			}
		}
		ErrorCatcher.get().setBuilding("");
		return true;
	}

	public void destroy(Planet planet) {
		if (!canDestroy(planet)) {
			return;
		}
		planet.getGrid()[y][x].setBuilding(null);
		planet.getBuildings().remove(this);
		this.x = -1;
		this.y = -1;
		for (ResourceType resourceType : ResourceType.values()) {
			planet.getResources().get(resourceType).val -= type.config.resourcesDestroy.get(resourceType);
		}
		deactivate(planet);
	}

	public boolean isActive() {
		return active;
	}

	public boolean canActivate(Planet planet) {
		if (active){
			return false;
		}
		if (planet.getResources().get(ResourceType.dudes).val < type.config.dudesNeeded) {
			ErrorCatcher.get().setBuilding("Not enough dudes available.");
			return false;
		}
		ErrorCatcher.get().setBuilding("");
		return true;
	}

	public void activate(Planet planet) {
		if (!canActivate(planet)) {
			return;
		}

		active = true;

		planet.getResources().get(ResourceType.dudes).val -= type.config.dudesNeeded;
		planet.addDudesCapacity(type.config.dudesCapacityIncrease);
		planet.addSpaceShipsCapacity(type.config.spaceShipsCapacityIncrease);
	}

	public boolean canDeactivate(Planet planet) {
		if (!active){
			return false;
		}
		if (planet.getTotalDudes() > planet.getDudesCapacity() - type.config.dudesCapacityIncrease){
			ErrorCatcher.get().setBuilding("Not enough capacity to house all dudes.");
			System.out.println(planet.getTotalDudes() + " " + planet.getDudesCapacity() + " " + type.config.dudesCapacityIncrease);
			return false;
		}
		if (planet.getFreeSpaceShipCapacity() < type.config.spaceShipsCapacityIncrease){
			ErrorCatcher.get().setBuilding("Not enough capacity to store all space ships.");
			return false;
		}
		ErrorCatcher.get().setBuilding("");
		return true;
	}

	public void deactivate(Planet planet) {
		deactivate(planet, false);
	}

	public void deactivate(Planet planet, boolean force) {
		if (!force && !canDeactivate(planet)){
			return;
		}

		active = false;

		planet.getResources().get(ResourceType.dudes).val += type.config.dudesNeeded;
		planet.addDudesCapacity(-type.config.dudesCapacityIncrease);
		planet.addSpaceShipsCapacity(-type.config.spaceShipsCapacityIncrease);

		if (force){
			if (planet.getTotalDudes() > planet.getDudesCapacity()) {
				int diff = planet.getTotalDudes() - planet.getDudesCapacity();
				planet.getResources().get(ResourceType.dudes).val -= diff;
				planet.addTotalDudes(-diff);
			}
			while (planet.getFreeSpaceShipCapacity() < 0) {
				planet.getSpaceShips().get(MathUtils.random(planet.getSpaceShips().size() - 1)).destroy(planet.getUniverse(), true);
			}
		}
	}

	public void toggle(Planet planet){
		if (active){
			deactivate(planet);
		} else {
			activate(planet);
		}
	}
}
