package no.erlyberly.bootlegterraria;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.erlyberly.bootlegterraria.console.ConsoleHandler;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TiledGameMap;

public class GameMain extends Game {

    public static final boolean TEST = false;
    private static final String TEST_MAP = "testmaps/testmap3.tmx";

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private GameMap gameMap;
    private static ConsoleHandler consoleHandler;

    private static GameMain gameMainInstance;

    @Override
    public void create() {
        gameMainInstance = this;
        this.batch = new SpriteBatch();

        consoleHandler = new ConsoleHandler(this);

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        camera.position.set(800f, 500f, 0); //Unlocked cam for debugging
        this.camera.update();

        loadMap(GameMain.TEST ? GameMain.TEST_MAP : "map.tmx");

    }

    public void loadMap(final String map) {
        this.gameMap = new TiledGameMap(map);
        this.gameMap.spawnPlayer();
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.gameMap.dispose();
    }

    @Override
    public void render() {
        this.batch.setProjectionMatrix(this.camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.input.setOnscreenKeyboardVisible(true);

        this.camera.update();
        this.gameMap.update();
        this.gameMap.render(this.camera, this.hudCamera, this.batch);
        consoleHandler.draw();
    }


    public static GameMain inst() {
        return gameMainInstance;
    }

    public GameMap getGameMap() {
        return this.gameMap;
    }

    public OrthographicCamera getCamera() {
        return this.camera;
    }

    public OrthographicCamera getHudCamera() {
        return this.hudCamera;
    }

    public SpriteBatch getBatch() {
        return this.batch;
    }

    public static ConsoleHandler getConsoleHandler() {
        return consoleHandler;
    }
}
