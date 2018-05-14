package no.erlyberly.bootlegterraria;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import no.erlyberly.bootlegterraria.console.ConsoleHandler;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;
import no.erlyberly.bootlegterraria.world.TiledGameMap;

public class GameMain extends Game {

    public static final boolean TEST = false;
    private static final String TEST_MAP = "testmaps/testmap3.tmx";

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private OrthographicCamera hudCamera;
    private GameMap gameMap;
    private static ConsoleHandler consoleHandler;

    private static float cameraPixel;

    @Override
    public void create() {
        batch = new SpriteBatch();

        consoleHandler = new ConsoleHandler(this);

        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        this.hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        camera.position.set(800f, 500f, 0); //Unlocked cam for debugging
        camera.update();
        setCameraPixel(camera);

        loadMap(GameMain.TEST ? GameMain.TEST_MAP : "map.tmx");
    }

    public void loadMap(String map) {
        this.gameMap = new TiledGameMap(map);
    }

    @Override
    public void dispose() {
        batch.dispose();
        gameMap.dispose();
    }

    @Override
    public void render() {

        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.justTouched()) {
            Vector3 pos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            TileType type = gameMap.getTileTypeByLocation(1, pos.x, pos.y);

            if (type != null) {
                consoleHandler.log(
                    "Tile clicked: " + type.getName() + ", id: " + type.getId() + ", dmg: " + type.getDps() +
                    " coord: " + (int) (pos.x / TileType.TILE_SIZE) + "," + (int) (pos.y / TileType.TILE_SIZE));
            }
            else {
                consoleHandler.log("Not a tile");
            }

        }

        camera.update();
        gameMap.update();
        gameMap.render(camera, hudCamera, batch);
        consoleHandler.draw();
    }


    public static float getCameraPixel() {
        return cameraPixel;
    }

    public static void setCameraPixel(final OrthographicCamera pCamera) {

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();
        float CameraPixel;

        float targetRatio = (float) width / (float) height;
        float sourceRatio = pCamera.viewportWidth / pCamera.viewportHeight;

        if (targetRatio > sourceRatio) { CameraPixel = height / pCamera.viewportHeight; }
        else { CameraPixel = width / pCamera.viewportWidth; }

        cameraPixel = (int) CameraPixel;
    }


    public GameMap getGameMap() {
        return gameMap;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public OrthographicCamera getHudCamera() {
        return hudCamera;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public static ConsoleHandler getConsoleHandler() {
        return consoleHandler;
    }
}
