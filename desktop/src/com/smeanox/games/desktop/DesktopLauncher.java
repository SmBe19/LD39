package com.smeanox.games.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.smeanox.games.Consts;
import com.smeanox.games.LD39;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Consts.DESIGN_WIDTH;
		config.height = Consts.DESIGN_HEIGHT;
		config.title = Consts.GAME_NAME;
		new LwjglApplication(new LD39(), config);
	}
}
