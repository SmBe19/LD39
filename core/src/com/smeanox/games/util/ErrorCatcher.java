package com.smeanox.games.util;

import java.util.ArrayList;
import java.util.List;

public class ErrorCatcher {
	private static ErrorCatcher singleton;

	private String any, spaceShip, building, start;
	private List<ErrorHappenedListener> listeners;

	private ErrorCatcher(){
		listeners = new ArrayList<ErrorHappenedListener>();
	}

	public static ErrorCatcher get(){
		if (singleton == null) {
			singleton = new ErrorCatcher();
		}
		return singleton;
	}

	public void reset(){
		any = "";
		spaceShip = "";
		building = "";
		start = "";
		fire("");
	}

	public String getAny() {
		return any;
	}

	public String getSpaceShip() {
		return spaceShip;
	}

	public void setSpaceShip(String spaceShip) {
		this.spaceShip = spaceShip;
		any = spaceShip;
		fire(any);
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
		any = building;
		fire(any);
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
		any = start;
		fire(any);
	}

	public void addListener(ErrorHappenedListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ErrorHappenedListener listener){
		listeners.remove(listener);
	}

	public void fire(String error){
		for (ErrorHappenedListener listener : listeners) {
			listener.errorHappened(error);
		}
	}

	public interface ErrorHappenedListener {
		void errorHappened(String error);
	}
}
