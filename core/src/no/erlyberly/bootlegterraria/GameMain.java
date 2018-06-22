package no.erlyberly.bootlegterraria;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.erlyberly.bootlegterraria.console.ConsoleHandler;
import no.erlyberly.bootlegterraria.input.InputHandler;
import no.erlyberly.bootlegterraria.util.CancellableThreadScheduler;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TiledGameMap;

public class GameMain extends Game {

    public static final CancellableThreadScheduler SECONDARY_THREAD = new CancellableThreadScheduler();

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private TiledGameMap gameMap;
    private static ConsoleHandler consoleHandler;

    private static GameMain gameMainInstance;

    public static Color backgroundColor = Color.BLACK;

    public static final boolean HEADLESS;

    private static final String HEADLESS_FLAG = "-headless";
    private static final String MAP_ENV = "map";
    private static final String DEFAULT_MAP = "map.tmx";


    static {
        //suuuuuuuper hacky way of getting main's arg[] args
        final String headlessFlag = System.getProperty("sun.java.command", "");
        HEADLESS = headlessFlag.contains(HEADLESS_FLAG);
    }

    @Override
    public void create() {
        gameMainInstance = this; //must be first
        this.batch = new SpriteBatch();


        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        camera.position.put(800f, 500f, 0); //Unlocked cam for debugging
        this.camera.update();


        consoleHandler = new ConsoleHandler(); //must be last


        String map = System.getenv(MAP_ENV);
        if (map == null) {
            consoleHandler.log("No environment map specified, loading default (" + DEFAULT_MAP + ")");
            map = DEFAULT_MAP;
        }
        loadMap(map);
    }

    public void loadMap(final String map) {
        SECONDARY_THREAD.cancelTasks();
        consoleHandler.log("Loading map '" + map + '\'');
        this.gameMap = new TiledGameMap(map, false);
        this.gameMap.spawnPlayer();
        this.camera.position.set(this.gameMap.getPlayer().getPos(), 0);
        new InputHandler();
        consoleHandler.resetInputProcessing();
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.gameMap.dispose();
    }

    @Override
    public void render() {
        this.batch.setProjectionMatrix(this.camera.combined);

        Gdx.gl.glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

    public static ConsoleHandler consHldr() {
        return consoleHandler;
    }
}
