package com.smeanox.games.world;

import com.smeanox.games.util.Rapper;

import java.util.EnumMap;

public class SpaceShip {

	private final SpaceShipType type;
	private String name;
	private Planet start, destination;
	private float distance, progress;
	private boolean inSpace;
	private float weight;
	private EnumMap<ResourceType, Rapper<Float>> resources;

	// TODO add ship construction and ship cost
	public SpaceShip(SpaceShipType type, String name, Planet start) {
		this.type = type;
		this.name = name;
		this.start = start;
		this.destination = start;
		start.getSpaceShips().add(this);

		resources = new EnumMap<ResourceType, Rapper<Float>>(ResourceType.class);
		for (ResourceType resourceType : ResourceType.values()) {
			resources.put(resourceType, new Rapper<Float>(0f));
		}
	}

	public SpaceShipType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Planet getStart() {
		return start;
	}

	public Planet getDestination() {
		return destination;
	}

	public float getDistance() {
		return distance;
	}

	public float getProgress() {
		return progress;
	}

	public boolean isInSpace() {
		return inSpace;
	}

	public float getWeight() {
		return weight;
	}

	public EnumMap<ResourceType, Rapper<Float>> getResources() {
		return resources;
	}

	private void updateDistanceAndWeight() {
		distance = (float) Math.sqrt((destination.getX() - start.getX()) * (destination.getX() - start.getX()) +
				(destination.getY() - start.getY()) * (destination.getY() - start.getY()));
		weight = type.config.weight;
		for (ResourceType resourceType : ResourceType.values()) {
			weight += resources.get(resourceType).val * resourceType.weight;
		}
	}

	public boolean moveResource(ResourceType type, float amountPlanetToSpaceShip) {
		if (start.getResources().get(type).val < amountPlanetToSpaceShip || resources.get(type).val + amountPlanetToSpaceShip < 0) {
			return false;
		}
		start.getResources().get(type).val -= amountPlanetToSpaceShip;
		resources.get(type).val += amountPlanetToSpaceShip;
		if (type == ResourceType.dudes) {
			start.addTotalDudes((int) -amountPlanetToSpaceShip);
		}
		return true;
	}

	public boolean canStart(Planet destination) {
		if (inSpace) {
			return false;
		}
		this.destination = destination;
		updateDistanceAndWeight();
		float usage = type.config.propellantPerWeightAndDistance * distance * weight;
		if (usage > resources.get(ResourceType.propellant).val) {
			return false;
		}
		return true;
	}

	public void start(Planet destination) {
		if (inSpace) {
			return;
		}
		this.destination = destination;
		updateDistanceAndWeight();
		progress = 0;
		inSpace = true;
		start.getSpaceShips().remove(this);
	}

	public void step(Universe universe) {
		if (!inSpace) {
			return;
		}

		progress += type.config.speed;
		resources.get(ResourceType.propellant).val -= type.config.propellantPerWeightAndDistance * weight;

		if (progress >= distance) {
			arrive();
		}
	}

	private void arrive() {
		if (!inSpace) {
			return;
		}
		inSpace = false;
		start = destination;
		destination.getSpaceShips().add(this);
	}
}
