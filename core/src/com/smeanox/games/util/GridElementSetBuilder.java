package com.smeanox.games.util;

import com.smeanox.games.world.GridElementType;
import com.smeanox.games.world.ResourceType;

import java.util.EnumSet;

public class GridElementSetBuilder {

	private EnumSet<GridElementType> set;

	public GridElementSetBuilder() {
		set = EnumSet.noneOf(GridElementType.class);
	}

	public GridElementSetBuilder add(GridElementType type){
		set.add(type);
		return this;
	}

	public EnumSet<GridElementType> build(){
		return set;
	}
}
