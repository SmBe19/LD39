package com.smeanox.games.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.XmlReader;
import com.smeanox.games.Consts;
import com.smeanox.games.screen.Atlas;
import com.smeanox.games.world.SpaceShipConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpaceShipConfigReader {

	private static SpaceShipConfigReader singleton = null;
	private Map<String, SpaceShipConfig> configs;

	private SpaceShipConfigReader() {
		configs = new HashMap<String, SpaceShipConfig>();
	}

	private void read(String file) throws IOException {
		XmlReader xmlReader = new XmlReader();

		XmlReader.Element root = xmlReader.parse(Gdx.files.internal(file));
		for (XmlReader.Element spaceship : root.getChildrenByName("spaceship")) {
			String key = spaceship.getAttribute("key");
			TextureRegion textureRegion = Atlas.textures.atlas.findRegion(spaceship.getAttribute("texture"));
			TextureRegion smallTextureRegion = Atlas.textures.atlas.findRegion(spaceship.getAttribute("small_texture"));
			XmlReader.Element properties = spaceship.getChildByName("properties");
			configs.put(key, new SpaceShipConfig(
					textureRegion,
					smallTextureRegion,
					spaceship.getAttribute("name"),
					properties.getFloat("capacity"),
					properties.getFloat("propellant"),
					properties.getFloat("speed"),
					properties.getFloat("weight")));
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
