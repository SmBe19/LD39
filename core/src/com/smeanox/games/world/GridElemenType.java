package com.smeanox.games.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.smeanox.games.screen.Textures;

public enum GridElemenType {
	sand(0, 0, 10, 10),
	water(0, 0, 10, 10),
	metal(0, 0, 10, 10),
	oil(0, 0, 10, 10),
	gas(0, 0, 10, 10),
	coal(0, 0, 10, 10),
	;

	public final TextureRegion texture;

	GridElemenType(int x, int y, int width, int height) {
		this.texture = new TextureRegion(Textures.ground.texture, x, y, width, height);
	}
}
