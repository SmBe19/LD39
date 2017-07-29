package com.smeanox.games.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public enum Textures {
	space("img/space.png"),
	ground("img/ground.png"),
	buildings("img/buildings.png"),
	spaceships("img/spaceships.png"),
	planets("img/planets.png"),
	icons("img/icons.png"),
	;

	public final Texture texture;

	Textures(String path) {
		this.texture = new Texture(Gdx.files.internal(path));
	}
}
