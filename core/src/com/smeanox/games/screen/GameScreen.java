package com.smeanox.games.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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

	private final Stage buildStage, spaceStage, portStage;
	private final Skin skin;
	private Stage currentStage;
	private final BuildWidget buildWidget;
	private ResourceWidget resourceWidgetBuild, resourceWidgetPort;
	private final SpaceMapWidget spaceMapWidget;
	private final VerticalGroup shipListBuild, shipListSpace;
	private final VerticalGroup planetList;
	private final TextField planetNameTextField, spaceShipNameTextField;
	private final HorizontalGroup buildingsList;
	private final Image spaceShipImage;
	private final SelectBox<SpaceShipType> spaceShipTypeSelection;
	private final ScrollPane spaceMapScrollPane;

	private final Planet.PlanetListener shipArrivedListener;

	public GameScreen() {
		universe = new Universe();
		universe.bigBang();
		currentPlanet = universe.getEarth();
		unusedTime = 0;

		skin = new Skin();
		skin.addRegions(Atlas.ui.atlas);
		skin.addRegions(Atlas.textures.atlas);
		skin.load(Gdx.files.internal("uiskin.json"));

//		buildStage = new Stage(new ExtendViewport(Consts.DESIGN_WIDTH, Consts.DESIGN_HEIGHT));
//		spaceStage = new Stage(new ExtendViewport(Consts.DESIGN_WIDTH, Consts.DESIGN_HEIGHT));
//		portStage = new Stage(new ExtendViewport(Consts.DESIGN_WIDTH, Consts.DESIGN_HEIGHT));
		buildStage = new Stage(new ScreenViewport());
		spaceStage = new Stage(new ScreenViewport());
		portStage = new Stage(new ScreenViewport());

		buildWidget = new BuildWidget();
		resourceWidgetBuild = new ResourceWidget(skin, true);
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

		shipArrivedListener = new Planet.PlanetListener() {
			@Override
			public void spaceShipArrived(Planet planet, SpaceShip spaceShip) {
				initSpaceShipList(shipListBuild, planet.getSpaceShips());
			}
		};

		initUI();

		setCurrentPlanet(currentPlanet);
		activateStage(buildStage);
	}

	private void initUI() {
		initBuildStage();
		initSpaceStage();
		initPortStage();
	}

	private void initBuildStage() {
		Image background = new Image(skin.getDrawable("ui/background"));
		background.setScaling(Scaling.fill);
		background.setFillParent(true);
		buildStage.addActor(background);

		Table buildTable = new Table(skin);
		buildTable.setFillParent(true);
		buildStage.addActor(buildTable);
		buildTable.columnDefaults(0).width(150);
		buildTable.columnDefaults(2).width(150);

		buildTable.row().height(50);
		buildTable.add(resourceWidgetBuild).colspan(3).width(Value.prefWidth);
		buildTable.row();

		// planets
		// TODO fix alignment
		planetList.align(Align.left);
		Planet.PlanetListener discoveryListener = new Planet.PlanetListener() {
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
		for (Planet planet : universe.getPlanets()) {
			planet.addListener(discoveryListener);
			if (planet.isVisited()){
				discoveryListener.discovered(planet);
			}
		}
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
		ScrollPane buildScrollPane = new ScrollPane(buildWidget, skin, "no-bars");
		buildTable.add(buildScrollPane).expand();

		// spaceships
		shipListBuild.padRight(5);
		shipListBuild.align(Align.right);
		VerticalGroup shipStuff = new VerticalGroup();
		shipStuff.pad(5);
		shipStuff.addActor(shipListBuild);
		shipStuff.addActor(new Spacer(20));
		spaceShipTypeSelection.setItems(SpaceShipType.values());
		shipStuff.addActor(spaceShipTypeSelection);
		shipStuff.addActor(new Spacer(5));
		TextButton buyShipButton = new TextButton("Buy Ship", skin);
		buyShipButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SpaceShipType type = spaceShipTypeSelection.getSelected();
				if (SpaceShip.canBuild(type, currentPlanet)){
					SpaceShip ship = new SpaceShip(type, type + " ship " + MathUtils.random(1000));
					ship.build(universe, currentPlanet);
				}
			}
		});
		shipStuff.addActor(buyShipButton);
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
					}
				}));
		for (final BuildingType buildingType : BuildingType.values()) {
			buildingsList.addActor(createBuildingButton(new Image(buildingType.config.previewTexture),
					buildingType.config.name, new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							buildWidget.setCurrentBuildingType(buildingType);
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
		buildTable.add(errorLabel).left();

		buildTable.setDebug(Consts.DEBUG, true);
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
		building.add(preview).height(Value.percentHeight(0.4f)).width(Value.percentHeight(0.4f));
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

		spaceTable.setDebug(Consts.DEBUG, true);
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

		spaceShipNameTextField.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				currentSpaceShip.setName(((TextField) actor).getText());
			}
		});
		addEscToTextField(spaceShipNameTextField);
		portTable.add(spaceShipNameTextField).pad(5);
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

		portTable.setDebug(Consts.DEBUG, true);
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
		unusedTime += delta * 1000;
		while (unusedTime >= Consts.UNIVERSE_STEP_SIZE) {
			universe.step();
			unusedTime -= Consts.UNIVERSE_STEP_SIZE;
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
}
