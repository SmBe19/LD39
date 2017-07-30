package com.smeanox.games.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.smeanox.games.Consts;
import com.smeanox.games.LD39;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Consts.DESIGN_WIDTH, Consts.DESIGN_HEIGHT);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new LD39();
        }
}