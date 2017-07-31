package com.smeanox.games;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.smeanox.games.screen.GameScreen;

public class LD39 extends Game {

	@Override
	public void create() {
		setScreen(new GameScreen());
	}
}
