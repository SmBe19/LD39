package com.smeanox.games.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.smeanox.games.Consts;
import com.smeanox.games.util.ErrorCatcher;
import com.smeanox.games.world.BuildingType;
import com.smeanox.games.world.Planet;
import com.smeanox.games.world.SpaceShip;
import com.smeanox.games.world.SpaceShipType;
import com.smeanox.games.world.Universe;

import java.util.List;

import static com.badlogic.gdx.Gdx.input;

public class GameScreen implements Screen {

	private final Universe universe;
	private final ResourceWidget spaceMapResourceWidget;
	private final SpaceShipDetailWidget spaceShipDetailWidget;
	private final SpaceShipLoadingWidget spaceShipLoadingWidget;
	private float unusedTime;
	private Planet currentPlanet;
	private SpaceShip currentSpaceShip;
	private boolean selectingStart;
	private boolean handledWin;

	private final Stage buildStage, spaceStage, portStage, pauseStage;
	private final Skin skin;
	private Stage currentStage;
	private final BuildWidget buildWidget;
	private final ResourceWidget resourceWidgetBuild, resourceWidgetPort;
	private final ResourceInfoWidget resourceInfoWidget;
	private final SpaceMapWidget spaceMapWidget;
	private final VerticalGroup shipListBuild, shipListSpace;
	private final VerticalGroup planetList;
	private final TextField planetNameTextField, spaceShipNameTextField;
	private final HorizontalGroup buildingsList;
	private final Image spaceShipImage;
	private final SelectBox<SpaceShipType> spaceShipTypeSelection;
	private final ScrollPane spaceMapScrollPane;
	private final TextButton buyShipButton;
	private final Dialog winDialog, loseDialog, welcomeDialog;

	private final Planet.PlanetListener shipArrivedListener;
	private final Planet.PlanetListener discoveryListener;

	public GameScreen() {
		universe = new Universe();
		universe.bigBang();
		currentPlanet = universe.getEarth();
		unusedTime = 0;
		handledWin = false;

		skin = new Skin();
		skin.addRegions(Atlas.ui.atlas);
		skin.addRegions(Atlas.textures.atlas);
		skin.load(Gdx.files.internal("uiskin.json"));

		buildStage = new Stage(new ScreenViewport());
		spaceStage = new Stage(new ScreenViewport());
		portStage = new Stage(new ScreenViewport());
		pauseStage = new Stage(new ScreenViewport());

		buildWidget = new BuildWidget(skin);
		resourceWidgetBuild = new ResourceWidget(skin, true);
		resourceInfoWidget = new ResourceInfoWidget(skin);
		resourceWidgetPort = new ResourceWidget(skin, true);
		spaceMapResourceWidget = new ResourceWidget(skin, false);
		spaceMapWidget = new SpaceMapWidget(skin, universe);
		planetList = new VerticalGroup();
		shipListBuild = new VerticalGroup();
		shipListSpace = new VerticalGroup();
		buildingsList = new HorizontalGroup();
		planetNameTextField = new TextField("", skin);
		spaceShipNameTextField = new TextField("", skin);
		spaceShipImage = new Image();
		spaceShipDetailWidget = new SpaceShipDetailWidget(skin);
		spaceShipLoadingWidget = new SpaceShipLoadingWidget(skin);
		spaceShipTypeSelection = new SelectBox<SpaceShipType>(skin);
		spaceMapScrollPane = new ScrollPane(spaceMapWidget, skin, "no-bars");
		buyShipButton = new TextButton("Buy Ship", skin);

		winDialog = new MyDialog("Won", skin).text("\nCongratulations, you won!\n").button("Continue playing", 1).button("Start again", 2);
		loseDialog = new MyDialog("Lost", skin).text("\nYou lost, all your dudes are dead!\nTry again!\n").button("Start again", 2);
		welcomeDialog = new MyDialog("Mission to Unadexus", skin)
				.button("Let's go", 1)
				.text("\nHi there, adventurer!\nAfter years of exploitation of the earth there is almost nothing left.\n" +
						"The earth is low on oil and coal and due to pollution solar panels don't work anymore.\n" +
						"We are running out of power!\n\n" +
						"Humanity's last hope is to move to another planet and start over.\n" +
						"Our astronomer found a suitable planet with the beautiful name of Unadexus.\n" +
						"You are in charge of guiding this mission and bringing all remaining humans to Unadexus.\n" +
						"Good luck!\n");

		shipArrivedListener = new Planet.PlanetListener() {
			@Override
			public void spaceShipArrived(Planet planet, SpaceShip spaceShip) {
				initSpaceShipList(shipListBuild, planet.getSpaceShips());
			}
		};

		discoveryListener = new Planet.PlanetListener() {
			@Override
			public void discovered(final Planet planet) {
				PlanetWidget planetWidget = new PlanetWidget(planet, skin);
				planetWidget.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						setCurrentPlanet(planet);
					}
				});
				planetList.addActor(planetWidget);
			}
		};

		initUI();

		setCurrentPlanet(currentPlanet);
		if(Consts.DEBUG){
			activateStage(buildStage);
		} else {
			activateStage(pauseStage);
			welcomeDialog.show(pauseStage);
		}
	}

	private void initUI() {
		initBuildStage();
		initSpaceStage();
		initPortStage();
		initPauseStage();
	}

	private void initBuildStage() {
		Image background = new Image(skin.getDrawable("ui/background"));
		background.setScaling(Scaling.fill);
		background.setFillParent(true);
		buildStage.addActor(background);

		Table buildTable = new Table(skin);
		buildTable.setFillParent(true);
		buildStage.addActor(buildTable);
		buildTable.columnDefaults(0).width(200);
		buildTable.columnDefaults(2).width(200);

		buildTable.row().height(50);
		buildTable.add(resourceWidgetBuild).colspan(3).width(Value.prefWidth);
		buildTable.row();

		// planets
		// TODO fix alignment
		planetList.align(Align.left);
		addDiscoveryListeners();
		VerticalGroup planetStuff = new VerticalGroup();
		planetStuff.pad(5);
		planetStuff.align(Align.left);
		planetNameTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				currentPlanet.setName(((TextField) actor).getText());
			}
		});
		addEscToTextField(planetNameTextField);
		planetStuff.addActor(planetNameTextField);
		planetStuff.addActor(new Spacer(10));
		planetStuff.addActor(planetList);
		buildTable.add(planetStuff);

		// build grid
		buildWidget.setResourceInfoWidget(resourceInfoWidget);
		ScrollPane buildScrollPane = new ScrollPane(buildWidget, skin, "no-bars");
		buildTable.add(buildScrollPane).expand();

		// spaceships
		shipListBuild.padRight(5);
		shipListBuild.align(Align.right);
		VerticalGroup shipStuff = new VerticalGroup();
		shipStuff.pad(5);
		shipStuff.addActor(shipListBuild);
		Spacer spacer1 = new Spacer(20);
		shipStuff.addActor(spacer1);
		spaceShipTypeSelection.setItems(SpaceShipType.values());
		spaceShipTypeSelection.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				resourceInfoWidget.setSpaceShipType(spaceShipTypeSelection.getSelected());
			}
		});
		resourceInfoWidget.setSpaceShipType(spaceShipTypeSelection.getSelected());
		shipStuff.addActor(spaceShipTypeSelection);
		Spacer spacer2 = new Spacer(5);
		shipStuff.addActor(spacer2);
		buyShipButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SpaceShipType type = spaceShipTypeSelection.getSelected();
				if (SpaceShip.canBuild(type, currentPlanet)){
					SpaceShip ship = new SpaceShip(type, Universe.getRandomName(8));
					ship.build(universe, currentPlanet);
				}
			}
		});
		shipStuff.addActor(buyShipButton);
		Spacer spacer3 = new Spacer(5);
		shipStuff.addActor(spacer3);
		shipStuff.addActor(resourceInfoWidget);
		resourceInfoWidget.getActorsToHide().add(shipListBuild);
		resourceInfoWidget.getActorsToHide().add(spaceShipTypeSelection);
		resourceInfoWidget.getActorsToHide().add(buyShipButton);
		resourceInfoWidget.getActorsToHide().add(spacer1);
		resourceInfoWidget.getActorsToHide().add(spacer2);
		resourceInfoWidget.getActorsToHide().add(spacer3);
		buildTable.add(shipStuff);

		buildTable.row().height(100);

		// spacemap
		ImageButton spacemapButton = new ImageButton(skin.getDrawable("ui/button_spacemap"));
		spacemapButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				activateStage(spaceStage);
				centerSpaceMap();
			}
		});
		buildTable.add(spacemapButton);

		// buildings
		ScrollPane buildingsScrollPane = new ScrollPane(buildingsList, skin);
		buildingsList.addActor(createBuildingButton(new Image(skin.getDrawable("building/cancel")),
				"Cancel", new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						buildWidget.setCurrentDestroy(false);
						buildWidget.setCurrentBuildingType(null);
					}
				}));
		buildingsList.addActor(createBuildingButton(new Image(skin.getDrawable("building/destroy")),
				"Remove", new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						buildWidget.setCurrentDestroy(true);
						buildStage.setKeyboardFocus(buildWidget);
					}
				}));
		for (final BuildingType buildingType : BuildingType.values()) {
			buildingsList.addActor(createBuildingButton(new Image(buildingType.config.previewTexture),
					buildingType.config.name, new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							buildWidget.setCurrentBuildingType(buildingType);
							buildStage.setKeyboardFocus(buildWidget);
						}
					}));
		}
		buildTable.add(buildingsScrollPane);

		final Label errorLabel = new Label("", skin);
		ErrorCatcher.get().addListener(new ErrorCatcher.ErrorHappenedListener() {
			@Override
			public void errorHappened(String error) {
				errorLabel.setText(ErrorCatcher.get().getAny());
			}
		});
		errorLabel.setWrap(true);
		buildTable.add(errorLabel).left().pad(5);

		buildTable.setDebug(Consts.LAYOUT_DEBUG, true);
	}

	private void addDiscoveryListeners() {
		planetList.clearChildren();
		for (Planet planet : universe.getPlanets()) {
			planet.addListener(discoveryListener);
			if (planet.isVisited()){
				discoveryListener.discovered(planet);
			}
		}
	}

	private void addEscToTextField(TextField textField) {
		textField.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ESCAPE) {
					buildStage.setKeyboardFocus(null);
					spaceStage.setKeyboardFocus(null);
					portStage.setKeyboardFocus(null);
					return true;
				}
				return false;
			}
		});
	}

	private Button createBuildingButton(Image preview, String text, EventListener listener) {
		Button building = new Button(skin);
		preview.setScaling(Scaling.fit);
		building.add(preview).height(50).width(50);
		building.row();
		building.add(new Label(text, skin));
		building.addListener(listener);
		return building;
	}

	private void initSpaceStage() {
		Image background = new Image(skin.getDrawable("ui/background"));
		background.setScaling(Scaling.fill);
		background.setFillParent(true);
		spaceStage.addActor(background);

		spaceMapWidget.addListener(new SpaceMapWidget.PlanetClickedListener() {
			@Override
			public void clicked(SpaceMapWidget.PlanetClickedEvent event, Planet planet) {
				if(selectingStart){
					if(currentSpaceShip.canStart(planet)){
						currentSpaceShip.start(planet);
						setSelectingStart(false);
					}
				} else {
					if (planet.isVisited()) {
						setCurrentPlanet(planet);
						activateStage(buildStage);
					}
				}
			}
		});
		spaceMapWidget.setResourceWidget(spaceMapResourceWidget);
		spaceMapScrollPane.setFillParent(true);
		spaceStage.addActor(spaceMapScrollPane);

		Table spaceTable = new Table(skin);
		spaceTable.setFillParent(true);
		spaceStage.addActor(spaceTable);

		spaceTable.columnDefaults(0).width(150);
		spaceTable.columnDefaults(2).width(150);

		spaceTable.row().height(50);
		spaceTable.add();
		spaceTable.row();
		spaceTable.add(spaceMapResourceWidget).pad(10);
		spaceTable.add().expand();
		shipListSpace.pad(5);
		shipListSpace.align(Align.right);
		spaceTable.add(shipListSpace);
		spaceTable.row().height(100);
		ImageButton backButton = new ImageButton(skin.getDrawable("ui/button_back"));
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				activateStage(selectingStart ? portStage : buildStage);
				setSelectingStart(false);
			}
		});
		spaceTable.add(backButton);
		spaceTable.add();
		final Label errorLabel = new Label("", skin);
		ErrorCatcher.get().addListener(new ErrorCatcher.ErrorHappenedListener() {
			@Override
			public void errorHappened(String error) {
				errorLabel.setText(ErrorCatcher.get().getStart());
			}
		});
		errorLabel.setWrap(true);
		spaceTable.add(errorLabel);

		spaceTable.setDebug(Consts.LAYOUT_DEBUG, true);
	}

	private void initPortStage() {
		Image background = new Image(skin.getDrawable("ui/background"));
		background.setScaling(Scaling.fill);
		background.setFillParent(true);
		portStage.addActor(background);

		Table portTable = new Table(skin);
		portTable.setFillParent(true);
		portStage.addActor(portTable);
		portTable.columnDefaults(0).width(200);
		portTable.columnDefaults(2).width(200);

		portTable.row().height(50);
		portTable.add(resourceWidgetPort).colspan(3).width(Value.prefWidth);
		portTable.row();

		VerticalGroup shipStuff = new VerticalGroup();
		spaceShipNameTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				currentSpaceShip.setName(((TextField) actor).getText());
			}
		});
		addEscToTextField(spaceShipNameTextField);
		shipStuff.addActor(spaceShipNameTextField);
		shipStuff.addActor(new Spacer(20));
		TextButton shipDestroyButton = new TextButton("Destroy Ship", skin);
		shipDestroyButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				currentSpaceShip.destroy(universe);
				activateStage(buildStage);
			}
		});
		shipStuff.addActor(shipDestroyButton);
		portTable.add(shipStuff).pad(5);
		spaceShipImage.setScaling(Scaling.fit);
		portTable.add(spaceShipImage).expand();
		portTable.add(spaceShipDetailWidget);
		portTable.row().height(100);
		ImageButton backButton = new ImageButton(skin.getDrawable("ui/button_back"));
		backButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				activateStage(buildStage);
			}
		});
		portTable.add(backButton);
		portTable.add(spaceShipLoadingWidget);
		ImageButton launchButton = new ImageButton(skin.getDrawable("ui/button_launch"));
		launchButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setCurrentPlanet(currentSpaceShip.getStart());
				setSelectingStart(true);
				activateStage(spaceStage);
				centerSpaceMap();
			}
		});
		portTable.add(launchButton);

		portTable.setDebug(Consts.LAYOUT_DEBUG, true);
	}
	private void initPauseStage() {
		Image background = new Image(skin.getDrawable("ui/background"));
		background.setScaling(Scaling.fill);
		background.setFillParent(true);
		pauseStage.addActor(background);
	}

	private void centerSpaceMap(){
		// TODO fix not working on first call
		spaceMapWidget.centerOnCurrentPlanet(spaceMapScrollPane);
	}

	private void activateStage(Stage stage) {
		currentStage = stage;
		input.setInputProcessor(currentStage);
		setCurrentPlanet(currentPlanet);
		setCurrentSpaceShip(currentSpaceShip);
		initSpaceShipList(shipListSpace, universe.getSpaceShips());
	}

	@Override
	public void render(float delta) {
		update(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		currentStage.act(delta);
		currentStage.draw();
	}

	private void update(float delta) {
		if (currentStage == pauseStage){
			return;
		}
		unusedTime += delta * 1000;
		while (unusedTime >= Consts.UNIVERSE_STEP_SIZE) {
			universe.step();
			unusedTime -= Consts.UNIVERSE_STEP_SIZE;
		}

		handleWinLoss();
	}

	private void handleWinLoss() {
		if (handledWin){
			return;
		}
		if (universe.isWon()){
			activateStage(pauseStage);
			winDialog.show(pauseStage);
			handledWin = true;
		} else if (universe.isLost()){
			activateStage(pauseStage);
			loseDialog.show(pauseStage);
			handledWin = true;
		}
	}

	public Planet getCurrentPlanet() {
		return currentPlanet;
	}

	public void setCurrentPlanet(Planet currentPlanet) {
		if (this.currentPlanet != null){
			this.currentPlanet.removeListener(shipArrivedListener);
		}

		this.currentPlanet = currentPlanet;
		buildWidget.setPlanet(currentPlanet);
		resourceWidgetBuild.setPlanet(currentPlanet);
		spaceMapWidget.setCurrentPlanet(currentPlanet);
		resourceWidgetPort.setPlanet(currentPlanet);
		resourceInfoWidget.setPlanet(currentPlanet);

		if (this.currentPlanet != null) {
			planetNameTextField.setText(currentPlanet.getName());

			initSpaceShipList(shipListBuild, currentPlanet.getSpaceShips());
			this.currentPlanet.addListener(shipArrivedListener);
		}
	}

	private void initSpaceShipList(Group group, List<SpaceShip> ships) {
		group.clearChildren();
		for (final SpaceShip spaceShip : ships) {
			ShipWidget shipWidget = new ShipWidget(spaceShip, skin);
			shipWidget.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					setCurrentSpaceShip(spaceShip);
					activateStage(portStage);
				}
			});
			group.addActor(shipWidget);
		}
	}

	public boolean isSelectingStart() {
		return selectingStart;
	}

	public void setSelectingStart(boolean selectingStart) {
		this.selectingStart = selectingStart;
	}

	public SpaceShip getCurrentSpaceShip() {
		return currentSpaceShip;
	}

	public void setCurrentSpaceShip(SpaceShip currentSpaceShip) {
		this.currentSpaceShip = currentSpaceShip;
		if (this.currentSpaceShip == null){
			return;
		}
		spaceShipImage.setDrawable(new TextureRegionDrawable(currentSpaceShip.getType().config.texture));
		spaceShipNameTextField.setText(currentSpaceShip.getName());
		spaceShipDetailWidget.setSpaceShip(currentSpaceShip);
		spaceShipLoadingWidget.setSpaceShip(currentSpaceShip);
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {
		buildStage.getViewport().update(width, height, true);
		spaceStage.getViewport().update(width, height, true);
		portStage.getViewport().update(width, height, true);
		pauseStage.getViewport().update(width, height, true);
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
		buildStage.dispose();
		spaceStage.dispose();
		portStage.dispose();
		skin.dispose();
		for (Atlas atlas : Atlas.values()) {
			atlas.atlas.dispose();
		}
	}

	private class MyDialog extends Dialog {

		public MyDialog(String title, Skin skin) {
			super(title, skin);
		}

		@Override
		protected void result(Object object) {
			if (object.equals(1)) {
				Timer.schedule(new Timer.Task() {
					@Override
					public void run() {
						activateStage(buildStage);
					}
				}, 1);
				hide();
			} else if (object.equals(2)){
				universe.bigBang();
				addDiscoveryListeners();
				unusedTime = 0;
				handledWin = false;
				setCurrentPlanet(universe.getEarth());
				Timer.schedule(new Timer.Task() {
					@Override
					public void run() {
						activateStage(buildStage);
					}
				}, 1);
				hide();
			}
		}
	}
}
