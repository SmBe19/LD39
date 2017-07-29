package com.smeanox.games.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.smeanox.games.screen.Atlas;

public enum GridElementType {
	sand,
	water,
	metal,
	oil,
	gas,
	coal,
	;

	public final TextureRegion texture;

	GridElementType() {
		this.texture = Atlas.textures.atlas.findRegion("ground/" + this.name());
	}
}
