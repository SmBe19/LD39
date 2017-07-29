package com.smeanox.games.screen;


import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public enum Atlas {
	textures("textures.atlas"),
	ui("uiskin.atlas"),
	;

	public final TextureAtlas atlas;

	Atlas(String file) {
		this.atlas = new TextureAtlas(file);
	}
}
