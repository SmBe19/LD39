package com.smeanox.games.screen;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pools;
import com.smeanox.games.Consts;
import com.smeanox.games.world.Planet;
import com.smeanox.games.world.SpaceShip;
import com.smeanox.games.world.Universe;

public class SpaceMapWidget extends Widget {

	private final Skin skin;
	private final BitmapFont largeFont, smallFont;
	private final Universe universe;
	private final BitmapFontCache currentPlanetNameCache, hoverPlanetNameCache;
	private final BitmapFontCache hoverPlanetDistanceCache;
	private final Sprite sprite;
	private Planet currentPlanet, hoverPlanet;
	private float currentPlanetFontCenter, hoverPlanetNameFontCenter, hoverPlanetDistanceFontCenter;
	private ResourceWidget resourceWidget;
	private float prefWidth, prefHeight;
	private int offX, offY;
	private Vector2 start;
	private Vector2 destination;

	public SpaceMapWidget(Skin skin, final Universe universe) {
		this.skin = skin;
		this.universe = universe;

		int miX = Integer.MAX_VALUE, maX = Integer.MIN_VALUE;
		int miY = Integer.MAX_VALUE, maY = Integer.MIN_VALUE;
		for (Planet planet : universe.getPlanets()) {
			miX = Math.min(miX, planet.getX());
			maX = Math.max(maX, planet.getX());
			miY = Math.min(miY, planet.getY());
			maY = Math.max(maY, planet.getY());
		}
		offX = -miX + Consts.PLANET_PADDING;
		offY = -miY + Consts.PLANET_PADDING;
		prefWidth = 2 * Consts.PLANET_PADDING + maX - miX;
		prefHeight = 2 * Consts.PLANET_PADDING + maY - miY;

		largeFont = skin.getFont("font-arial32");
		smallFont = skin.getFont("font-arial16");
		currentPlanetNameCache = largeFont.newFontCache();
		hoverPlanetNameCache = largeFont.newFontCache();
		hoverPlanetDistanceCache = smallFont.newFontCache();
		sprite = new Sprite();
		start = new Vector2();
		destination = new Vector2();

		addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				Planet planet = hitPlanet(x, y);
				if (planet != null) {
					PlanetClickedEvent planetEvent = Pools.obtain(PlanetClickedEvent.class);
					planetEvent.setPlanet(planet);
					fire(planetEvent);
					Pools.free(planetEvent);
				}
			}
		});

		addListener(new InputListener() {
			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				setHoverPlanet(hitPlanet(x, y));
				return false;
			}
		});
	}

	protected Planet hitPlanet(float x, float y) {
		for (Planet planet : universe.getPlanets()) {
			if (offX + planet.getX() < x && x < offX + planet.getX() + Consts.PLANET_SIZE &&
					offY + planet.getY() < y && y < offY + planet.getY() + Consts.PLANET_SIZE) {
				return planet;
			}
		}
		return null;
	}

	public Planet getCurrentPlanet() {
		return currentPlanet;
	}

	public void setCurrentPlanet(Planet currentPlanet) {
		this.currentPlanet = currentPlanet;
		if (currentPlanet != null) {
			currentPlanetNameCache.setText(currentPlanet.getName(), 0, 0);
			currentPlanetFontCenter = offX + currentPlanet.getX() + (Consts.PLANET_SIZE - currentPlanetNameCache.getLayouts().get(0).width) * 0.5f;
		}
	}

	public Planet getHoverPlanet() {
		return hoverPlanet;
	}

	public void setHoverPlanet(Planet hoverPlanet) {
		this.hoverPlanet = hoverPlanet;
		if (resourceWidget != null) {
			resourceWidget.setPlanet(hoverPlanet);
		}
		if (hoverPlanet != null) {
			int dist = MathUtils.ceil((float) Math.sqrt((hoverPlanet.getX() - currentPlanet.getX()) * (hoverPlanet.getX() - currentPlanet.getX())
					+ (hoverPlanet.getY() - currentPlanet.getY()) * (hoverPlanet.getY() - currentPlanet.getY())));
			hoverPlanetNameCache.setText(hoverPlanet.getName(), 0, 0);
			hoverPlanetDistanceCache.setText(dist + " km", 0, 0);
			hoverPlanetNameFontCenter = offX + hoverPlanet.getX() + (Consts.PLANET_SIZE - hoverPlanetNameCache.getLayouts().get(0).width) * 0.5f;
			hoverPlanetDistanceFontCenter = offX + hoverPlanet.getX() + (Consts.PLANET_SIZE - hoverPlanetDistanceCache.getLayouts().get(0).width) * 0.5f;
		}
	}

	public ResourceWidget getResourceWidget() {
		return resourceWidget;
	}

	public void setResourceWidget(ResourceWidget resourceWidget) {
		this.resourceWidget = resourceWidget;
	}

	@Override
	public float getPrefWidth() {
		return prefWidth;
	}

	@Override
	public float getPrefHeight() {
		return prefHeight;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		float ax = getX();
		float ay = getY();

		batch.setColor(1, 1, 1, 1);

		for (Planet planet : universe.getPlanets()) {
			if (!planet.isVisited()){
				batch.setColor(0.5f, 0.5f, 0.5f, 1);
			}

			batch.draw(planet.getTexture(), ax + offX + planet.getX(), ay + offY + planet.getY(), Consts.PLANET_SIZE, Consts.PLANET_SIZE);

			batch.setColor(1, 1, 1, 1);
		}

		if (currentPlanet != null) {
			currentPlanetNameCache.setPosition(ax + currentPlanetFontCenter, ay + offY + currentPlanet.getY() + Consts.PLANET_SIZE + largeFont.getCapHeight() + 10);
			currentPlanetNameCache.draw(batch);
		}
		if (hoverPlanet != null && hoverPlanet != currentPlanet) {
			hoverPlanetNameCache.setPosition(ax + hoverPlanetNameFontCenter, ay + offY + hoverPlanet.getY() + Consts.PLANET_SIZE + largeFont.getCapHeight() + 10);
			hoverPlanetDistanceCache.setPosition(ax + hoverPlanetDistanceFontCenter, ay + offY + hoverPlanet.getY() + Consts.PLANET_SIZE + largeFont.getCapHeight() + smallFont.getCapHeight() + 20);
			hoverPlanetNameCache.draw(batch);
			hoverPlanetDistanceCache.draw(batch);
		}

		for (SpaceShip ship : universe.getSpaceShips()) {
			if (!ship.isInSpace()) {
				continue;
			}
			sprite.setRegion(ship.getType().config.smallTexture);
			sprite.setSize(Consts.SPACESHIP_SIZE, Consts.SPACESHIP_SIZE);
			start.set(ship.getStart().getX() + Consts.PLANET_SIZE * 0.5f, ship.getStart().getY() + Consts.PLANET_SIZE * 0.5f);
			destination.set(ship.getDestination().getX() + Consts.PLANET_SIZE * 0.5f, ship.getDestination().getY() + Consts.PLANET_SIZE * 0.5f);
			sprite.setRotation(destination.angle(start));
			start.interpolate(destination, ship.getProgress() / ship.getDistance(), Interpolation.fade);
			sprite.setPosition(ax + offX + start.x, ay + offY + start.y);
			sprite.draw(batch);
		}
	}

	public static class PlanetClickedEvent extends Event {
		private Planet planet;

		public PlanetClickedEvent() {
		}

		public Planet getPlanet() {
			return planet;
		}

		public void setPlanet(Planet planet) {
			this.planet = planet;
		}
	}

	public void centerOnCurrentPlanet(ScrollPane scrollPane){
		scrollPane.setScrollX(offX + currentPlanet.getX() - scrollPane.getWidth() * 0.5f);
		scrollPane.setScrollY(prefHeight - (offY + currentPlanet.getY()) - scrollPane.getHeight() * 0.5f);
	}

	public static abstract class PlanetClickedListener implements EventListener {

		@Override
		public boolean handle(Event event) {
			if (!(event instanceof PlanetClickedEvent)) {
				return false;
			}
			PlanetClickedEvent planetEvent = (PlanetClickedEvent) event;
			clicked(planetEvent, planetEvent.getPlanet());
			return true;
		}

		public abstract void clicked(PlanetClickedEvent event, Planet planet);
	}
}
