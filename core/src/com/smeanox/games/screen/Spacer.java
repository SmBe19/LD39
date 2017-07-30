package com.smeanox.games.screen;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

public class Spacer extends Widget {

	private final float prefSize;

	public Spacer(float prefSize) {
		this.prefSize = prefSize;
	}

	@Override
	public float getPrefWidth() {
		return prefSize;
	}

	@Override
	public float getPrefHeight() {
		return prefSize;
	}
}
