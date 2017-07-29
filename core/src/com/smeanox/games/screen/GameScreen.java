package com.smeanox.games.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.smeanox.games.Consts;
import com.smeanox.games.world.Universe;

public class GameScreen implements Screen {

	private SpriteBatch batch;
	private Camera camera;
	private Universe universe;
	private float unusedTime;

	public GameScreen() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Consts.DESIGN_WIDTH, Consts.DESIGN_HEIGHT);

		universe = new Universe();
		unusedTime = 0;
	}

	@Override
	public void render(float delta) {
		update(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.update();
		batch.setProjectionMatrix(camera.combined);
	}

	private void update(float delta) {
		unusedTime += delta;
		while(unusedTime >= Consts.UNIVERSE_STEP_SIZE){
			universe.step();
			unusedTime -= Consts.UNIVERSE_STEP_SIZE;
		}
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {
		float ratio = (float) width / height;
		camera.viewportWidth = ratio * Consts.DESIGN_HEIGHT;
		camera.viewportHeight = Consts.DESIGN_HEIGHT;
		camera.update();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
