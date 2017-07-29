package com.smeanox.games.world;

public class Building {

	private final BuildingType type;
	private int x, y;
	private boolean active;

	public Building(BuildingType type) {
		this.type = type;
		this.x = -1;
		this.y = -1;
	}

	public BuildingType getType() {
		return type;
	}

	public boolean canBuild(Planet planet, int x, int y) {
		if (!type.config.needGridElementType.contains(planet.getGrid()[y][x].getType())) {
			return false;
		}
		for (ResourceType resourceType : ResourceType.values()) {
			if (planet.getResources().get(resourceType).val < type.config.resourcesBuild.get(resourceType)){
				return false;
			}
		}
		return true;
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
		planet.addDudesCapacity(type.config.dudesCapacityIncrease);
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
			deactivate(planet);
			return;
		}
		for (ResourceType resourceType : ResourceType.values()) {
			planet.getResources().get(resourceType).val -= type.config.resourcesUsage.get(resourceType);
		}
		planet.getGrid()[y][x].addLevel(-type.config.levelUsage);
	}

	public boolean canDestroy(Planet planet) {
		for (ResourceType resourceType : ResourceType.values()) {
			if (planet.getResources().get(resourceType).val < type.config.resourcesDestroy.get(resourceType)){
				return false;
			}
		}
		if (planet.getDudesCapacity() - type.config.dudesCapacityIncrease < planet.getTotalDudes()){
			return false;
		}
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
		planet.addDudesCapacity(-type.config.dudesCapacityIncrease);
	}

	public boolean canActivate(Planet planet) {
		if (active){
			return false;
		}
		if (planet.getResources().get(ResourceType.dudes).val < type.config.dudesNeeded) {
			return false;
		}
		return true;
	}

	public void activate(Planet planet) {
		if (!canActivate(planet)) {
			return;
		}

		planet.getResources().get(ResourceType.dudes).val -= type.config.dudesNeeded;
	}

	public boolean canDeactivate(Planet planet) {
		return active;
	}

	public void deactivate(Planet planet) {
		if (!canDeactivate(planet)){
			return;
		}

		planet.getResources().get(ResourceType.dudes).val += type.config.dudesNeeded;
	}
}
