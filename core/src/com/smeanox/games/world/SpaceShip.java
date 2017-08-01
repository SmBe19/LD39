package com.smeanox.games.world;

import com.smeanox.games.util.ErrorCatcher;
import com.smeanox.games.util.Rapper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class SpaceShip {

	private final SpaceShipType type;
	private String name;
	private Planet start, destination;
	private float distance, progress;
	private boolean inSpace;
	private float weight;
	private EnumMap<ResourceType, Rapper<Float>> resources;
	private List<SpaceShipListener> listeners;

	public SpaceShip(SpaceShipType type, String name) {
		this.type = type;
		this.name = name;

		listeners = new ArrayList<SpaceShipListener>();

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
		fireNameChanged();
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

	public static boolean canBuild(SpaceShipType type, Planet planet){
		for (ResourceType resourceType : ResourceType.values()) {
			if (planet.getResources().get(resourceType).val < type.config.resourcesBuild.get(resourceType)){
				ErrorCatcher.get().setSpaceShip("Not enough " + resourceType + ".");
				return false;
			}
		}
		if (planet.getFreeSpaceShipCapacity() < 1){
			ErrorCatcher.get().setSpaceShip("Not enough capacity to store space ship.");
			return false;
		}
		ErrorCatcher.get().setSpaceShip("");
		return true;
	}

	public boolean canBuild(Planet planet){
		return SpaceShip.canBuild(type, planet);
	}

	public void build(Universe universe, Planet planet) {
		if (!canBuild(planet)) {
			return;
		}
		start = planet;
		destination = planet;
		inSpace = false;
		for (ResourceType resourceType : ResourceType.values()) {
			planet.getResources().get(resourceType).val -= type.config.resourcesBuild.get(resourceType);
		}
		universe.getSpaceShips().add(this);
		start.getSpaceShips().add(this);

		updateDistanceAndWeight();

		start.fireSpaceShipsChanged(this);
	}

	public boolean canDestroy(Planet planet) {
		if (inSpace){
			ErrorCatcher.get().setSpaceShip("Space ship can't be destroyed while flying.");
			return false;
		}
		for (ResourceType resourceType : ResourceType.values()) {
			if (planet.getResources().get(resourceType).val + resources.get(resourceType).val < type.config.resourcesDestroy.get(resourceType)){
				ErrorCatcher.get().setSpaceShip("Not enough " + resourceType + ".");
				return false;
			}
		}
		ErrorCatcher.get().setSpaceShip("");
		return true;
	}

	public void destroy(Universe universe) {
		destroy(universe, false);
	}

	public void destroy(Universe universe, boolean force){
		if (!force && !canDestroy(start)){
			return;
		}

		universe.getSpaceShips().remove(this);
		start.getSpaceShips().remove(this);
		start.fireSpaceShipsChanged(this);

		for (ResourceType resourceType : ResourceType.values()) {
			start.getResources().get(resourceType).val -= type.config.resourcesDestroy.get(resourceType) - resources.get(resourceType).val;
		}
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
		if (weight - getType().config.weight + amountPlanetToSpaceShip * type.weight > getType().config.capacity) {
			return false;
		}
		if (type == ResourceType.dudes && amountPlanetToSpaceShip < 0 && start.getTotalDudes() - amountPlanetToSpaceShip > start.getDudesCapacity()){
			return false;
		}
		start.getResources().get(type).val -= amountPlanetToSpaceShip;
		resources.get(type).val += amountPlanetToSpaceShip;
		if (type == ResourceType.dudes) {
			start.addTotalDudes((int) -amountPlanetToSpaceShip);
		}
		updateDistanceAndWeight();
		fireFreightChanged();
		return true;
	}

	public boolean canStart(Planet destination) {
		if (inSpace) {
			ErrorCatcher.get().setStart("Space ship is already flying.");
			return false;
		}
		if (destination == this.start){
			ErrorCatcher.get().setSpaceShip("Destination has to be different from start planet.");
			return false;
		}
		this.destination = destination;
		updateDistanceAndWeight();
		float usage = type.config.propellantPerWeightAndDistance * distance * weight;
		if (usage > resources.get(ResourceType.propellant).val) {
			ErrorCatcher.get().setStart("Not enough propellant to reach destination.");
			return false;
		}
		if (destination.getFreeSpaceShipCapacity() < 1 && destination.isVisited()){
			ErrorCatcher.get().setStart("Not enough capacity on the destination planet.");
			return false;
		}
		ErrorCatcher.get().setStart("");
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
		destination.getArrivingSpaceShips().add(this);
		fireStateChanged();
		start.fireSpaceShipsChanged(this);
	}

	public void step(Universe universe) {
		if (!inSpace) {
			return;
		}

		float moveDist = Math.min(type.config.speed, distance - progress);
		progress += type.config.speed;
		resources.get(ResourceType.propellant).val -= type.config.propellantPerWeightAndDistance * moveDist * weight;

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
		if (!destination.isVisited()){
			destination.discoverPlanet();
		}
		destination.getArrivingSpaceShips().remove(this);
		destination.getSpaceShips().add(this);
		fireStateChanged();
		destination.fireSpaceShipsChanged(this);
	}

	public void addListener(SpaceShipListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SpaceShipListener listener) {
		listeners.remove(listener);
	}

	public void fireStateChanged (){
		for (SpaceShipListener listener : listeners) {
			listener.spaceShipStateChanged(this);
		}
	}

	public void fireNameChanged () {
		for (SpaceShipListener listener : listeners) {
			listener.spaceShipNameChanged(this);
		}
	}

	public void fireFreightChanged () {
		for (SpaceShipListener listener : listeners) {
			listener.spaceShipFreightChanged(this);
		}
	}

	public static abstract class SpaceShipListener {
		public void spaceShipStateChanged(SpaceShip spaceShip){}

		public void spaceShipNameChanged(SpaceShip spaceShip){}

		public void spaceShipFreightChanged(SpaceShip spaceShip) {}
	}
}
