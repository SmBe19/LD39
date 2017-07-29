package com.smeanox.games.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.smeanox.games.Consts;
import com.smeanox.games.screen.Textures;
import com.smeanox.games.world.BuildingConfig;
import com.smeanox.games.world.GridElemenType;
import com.smeanox.games.world.ResourceType;

import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class BuildingConfigReader {

	private static BuildingConfigReader singleton = null;
	private Map<String, BuildingConfig> configs;

	private BuildingConfigReader() {
		configs = new HashMap<String, BuildingConfig>();
	}

	private EnumSet<GridElemenType> readElementList(XmlReader.Element element) {
		EnumSet<GridElemenType> result = EnumSet.noneOf(GridElemenType.class);
		for (XmlReader.Element el : element.getChildrenByName("element")) {
			result.add(GridElemenType.valueOf(el.getAttribute("type")));
		}
		return result;
	}

	private EnumMap<ResourceType, Float> readResourceList(XmlReader.Element element) {
		EnumMap<ResourceType, Float> result = new EnumMap<ResourceType, Float>(ResourceType.class);
		for (XmlReader.Element resource : element.getChildrenByName("resource")) {
			result.put(ResourceType.valueOf(resource.getAttribute("type")), resource.getFloat("amount"));
		}
		return result;
	}

	private void read(String file) throws IOException {
		XmlReader xmlReader = new XmlReader();

		XmlReader.Element root = xmlReader.parse(Gdx.files.internal(file));
		for (XmlReader.Element building : root.getChildrenByName("building")) {
			String key = building.getAttribute("key");
			XmlReader.Element texture = building.getChildByName("texture");
			TextureRegion textureRegion = new TextureRegion(Textures.valueOf(texture.getAttribute("id")).texture,
					texture.getInt("x"), texture.getInt("y"),
					texture.getInt("width"), texture.getInt("height"));
			XmlReader.Element properties = building.getChildByName("properties");
			XmlReader.Element needGridElement = building.getChildByName("needGridElement");
			XmlReader.Element resourcesBuild = building.getChildByName("resourcesBuild");
			XmlReader.Element resourcesDestroy = building.getChildByName("resourcesDestroy");
			XmlReader.Element resourcesUsage = building.getChildByName("resourcesUsage");

			configs.put(key, new BuildingConfig(textureRegion,
					properties.getInt("width"), properties.getInt("height"),
					properties.getInt("dudes_needed"),
					properties.getInt("dudes_capacity_increase"),
					properties.getInt("spaceships_increase"),
					properties.getFloat("level_usage"),
					readElementList(needGridElement),
					readResourceList(resourcesBuild),
					readResourceList(resourcesDestroy),
					readResourceList(resourcesUsage)));
		}
	}

	public static BuildingConfig get(String key) {
		if (singleton == null) {
			singleton = new BuildingConfigReader();
			try {
				singleton.read(Consts.BUILDINGS_CONFIG);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return singleton.configs.get(key);
	}
}
