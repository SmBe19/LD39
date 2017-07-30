package com.smeanox.games.util;

import com.smeanox.games.world.GridElementType;

import java.util.EnumMap;
import java.util.EnumSet;

public class GridElementMapBuilder<T> {

	private EnumMap<GridElementType, T> map;

	public GridElementMapBuilder() {
		map = new EnumMap<GridElementType, T>(GridElementType.class);
	}

	public GridElementMapBuilder<T> add(GridElementType type, T val){
		map.put(type, val);
		return this;
	}

	public EnumMap<GridElementType, T> build(){
		return map;
	}
}
