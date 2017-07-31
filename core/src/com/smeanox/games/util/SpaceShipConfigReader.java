package com.smeanox.games.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.smeanox.games.Consts;
import com.smeanox.games.screen.Atlas;
import com.smeanox.games.world.ResourceType;
import com.smeanox.games.world.SpaceShipConfig;

import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SpaceShipConfigReader {

	private static SpaceShipConfigReader singleton = null;
	private Map<String, SpaceShipConfig> configs;

	private SpaceShipConfigReader() {
		configs = new HashMap<String, SpaceShipConfig>();
	}

	private EnumMap<ResourceType, Float> readResourceList(XmlReader.Element element, boolean convert) {
		EnumMap<ResourceType, Float> result = new EnumMap<ResourceType, Float>(ResourceType.class);
		for (ResourceType resourceType : ResourceType.values()) {
			result.put(resourceType, 0f);
		}
		for (XmlReader.Element resource : element.getChildrenByName("resource")) {
			result.put(ResourceType.valueOf(resource.getAttribute("type")), resource.getFloat("amount") * (convert ? Consts.UNIVERSE_TIME_MULTIPLIER : 1));
		}
		return result;
	}

	private void read(String file) throws IOException {
		XmlReader xmlReader = new XmlReader();

		XmlReader.Element root = xmlReader.parse(Gdx.files.internal(file));
		for (XmlReader.Element spaceship : root.getChildrenByName("spaceship")) {
			String key = spaceship.getAttribute("key");
			TextureRegion textureRegion = Atlas.textures.atlas.findRegion(spaceship.getAttribute("texture", "spaceship/" + key));
			TextureRegion smallTextureRegion = Atlas.textures.atlas.findRegion(spaceship.getAttribute("small_texture", "spaceship/small_" + key));
			XmlReader.Element properties = spaceship.getChildByName("properties");
			XmlReader.Element resourcesBuild = spaceship.getChildByName("resourcesBuild");
			XmlReader.Element resourcesDestroy = spaceship.getChildByName("resourcesDestroy");
			configs.put(key, new SpaceShipConfig(
					textureRegion,
					smallTextureRegion,
					spaceship.getAttribute("name"),
					properties.getFloat("capacity"),
					properties.getFloat("propellant") * Consts.UNIVERSE_TIME_MULTIPLIER,
					properties.getFloat("speed") * Consts.UNIVERSE_TIME_MULTIPLIER,
					properties.getFloat("weight"),
					readResourceList(resourcesBuild, false),
					readResourceList(resourcesDestroy, false)));
		}
	}

	public static SpaceShipConfig get(String key) {
		if (singleton == null) {
			singleton = new SpaceShipConfigReader();
			try {
				singleton.read(Consts.SPACESHIPS_CONFIG);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return singleton.configs.get(key);
	}
}
