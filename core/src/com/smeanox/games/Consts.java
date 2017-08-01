package com.smeanox.games;

import com.smeanox.games.util.GridElementMapBuilder;
import com.smeanox.games.util.ResourceMapBuilder;
import com.smeanox.games.world.GridElementType;
import com.smeanox.games.world.ResourceType;

import java.util.EnumMap;

public class Consts {

	public static final boolean LAYOUT_DEBUG = false;
	public static final boolean DEBUG = false;
	public static final float DEBUG_RESOURCE_ADD = 100000;
	public static final float CHEAT_RESOURCE_ADD = 100;

	public static final String GAME_NAME = "Unadexus";
	public static final int DESIGN_WIDTH = 800;
	public static final int DESIGN_HEIGHT = 480;
	public static final float DESIGN_DPI = 0.6f;

	public static final String BUILDINGS_CONFIG = "cfg/buildings.xml";
	public static final String SPACESHIPS_CONFIG = "cfg/spaceships.xml";

	public static final int UNIVERSE_STEP_SIZE = 100;
	public static final float UNIVERSE_TIME_MULTIPLIER = Consts.UNIVERSE_STEP_SIZE / 1000f;

	public static final float GRID_WIDTH = 50;
	public static final float GRID_HEIGHT = 50;
	public static final float RESOURCE_ICON_SIZE = 0.8f;
	public static final float RESOURCE_ICON_PADDING = 0.1f;
	public static final float RESOURCE_WIDTH = 70;
	public static final float RESOURCE_HEIGHT = 20;
	public static final int PLANET_PADDING = 200;
	public static final float PLANET_SIZE = 50;
	public static final float SPACESHIP_SIZE = 10;
	public static final int LOADING_RESOURCE_PER_LINE = 3;
	public static final int DOUBLE_CLICK_TIME = 500;

	public static final int UNIVERSE_SIZE = 1234;
	public static final int PLANET_MIN_DIST = 200;
	public static final int DUDES_START_COUNT = 200;
	public static final int DUDE_INCREASE_TIME = 30000;
	public static final float DUDE_INCREASE_AMOUNT = 0.05f;
	public static final EnumMap<ResourceType, Float> RESOURCE_START
			= new ResourceMapBuilder<Float>()
			.add(ResourceType.electricity, 2000f)
			.add(ResourceType.metal, 500f)
			.add(ResourceType.propellant, 100f)
			.add(ResourceType.water, 0f)
			.add(ResourceType.dudes, (float) DUDES_START_COUNT)
			.add(ResourceType.solarpanel, 0f)
			.add(ResourceType.oil, 0f)
			.add(ResourceType.gas, 0f)
			.add(ResourceType.coal, 0f)
			.build();
	public static final int PLANET_COUNT = 20;
	public static final EnumMap<GridElementType, Integer> PLANET_ELEMENTS_COUNT
			= new GridElementMapBuilder<Integer>()
			.add(GridElementType.sand, PLANET_COUNT)
			.add(GridElementType.water, 8)
			.add(GridElementType.metal, 12)
			.add(GridElementType.oil, 12)
			.add(GridElementType.gas, 12)
			.add(GridElementType.coal, 12)
			.build();
	public static final EnumMap<GridElementType, Float> ELEMENT_FLOW_AMOUNT
			= new GridElementMapBuilder<Float>()
			.add(GridElementType.sand, 0f)
			.add(GridElementType.water, 0.05f)
			.add(GridElementType.metal, 0.05f)
			.add(GridElementType.oil, 0.06f)
			.add(GridElementType.gas, 0.06f)
			.add(GridElementType.coal, 0.06f)
			.build();
	public static final EnumMap<GridElementType, Float> ELEMENT_FLOW_CONTINUE
			= new GridElementMapBuilder<Float>()
			.add(GridElementType.sand, 0f)
			.add(GridElementType.water, 0.8f)
			.add(GridElementType.metal, 0.5f)
			.add(GridElementType.oil, 0.8f)
			.add(GridElementType.gas, 0.8f)
			.add(GridElementType.coal, 0.8f)
			.build();
	public static final float PLANET_SMALL_CHANCE = 0.7f;
	public static final float PLANET_SMALL_MULTIPLIER = 0.25f;
	public static final int GRID_MIN_SIZE = 2;
	public static final int GRID_MAX_SIZE = 40;
	public static final float SOLAR_MULTIPLIER_MIN = 0;
	public static final float SOLAR_MULTIPLIER_MAX = 10;
	public static final float START_LEVEL = 300;
	public static final int SPACE_SHIP_CAPACITY_WITHOUT_PORT = 1;
}
