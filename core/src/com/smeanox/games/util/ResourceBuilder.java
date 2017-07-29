package com.smeanox.games.util;

import com.smeanox.games.world.ResourceType;

import java.util.EnumMap;

public class ResourceBuilder<T> {

	private EnumMap<ResourceType, T> map;

	public ResourceBuilder() {
		map = new EnumMap<ResourceType, T>(ResourceType.class);
	}

	public ResourceBuilder<T> add(ResourceType type, T val){
		map.put(type, val);
		return this;
	}

	public EnumMap<ResourceType, T> build(){
		return map;
	}
}
