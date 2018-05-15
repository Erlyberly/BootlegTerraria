package no.erlyberly.bootlegterraria;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
        this.batch = new SpriteBatch();

        consoleHandler = new ConsoleHandler(this);

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        this.hudCamera = new OrthographicCamera();
        this.hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        camera.position.set(800f, 500f, 0); //Unlocked cam for debugging
        this.camera.update();
        setCameraPixel(this.camera);

        loadMap(GameMain.TEST ? GameMain.TEST_MAP : "map.tmx");
    }

    public void loadMap(final String map) {
        this.gameMap = new TiledGameMap(map);
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
        if (Gdx.input.isTouched()) {

            final Vector3 pos = this.camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            final TileType type = this.gameMap.getTileTypeByLocation(1, pos.x, pos.y);

            final int blockX = (int) (pos.x / TileType.TILE_SIZE);
            final int blockY = (int) (pos.y / TileType.TILE_SIZE);

            if (Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT)) {
                TileType tt = null;

                if (Gdx.input.justTouched()) {
                    final int id = (type != null ? type.getId() : 0) + 1;
                    tt = TileType.getTileTypeById(id);
                }
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || tt != null) {
                    this.gameMap.setBlockAt(blockX, blockY, tt);
                }
            }
            else if (Gdx.input.justTouched()) {
                if (type != null) {
                    consoleHandler.log(
                        "Tile clicked: " + type.getName() + ", id: " + type.getId() + ", dmg: " + type.getDps() +
                        " coord: (" + blockX + ", " + blockY + ")");
                }
                else {
                    consoleHandler.log("Not a tile");
                }
            }
        }

        this.camera.update();
        this.gameMap.update();
        this.gameMap.render(this.camera, this.hudCamera, this.batch);
        consoleHandler.draw();
    }


    public static float getCameraPixel() {
        return cameraPixel;
    }

    public static void setCameraPixel(final OrthographicCamera pCamera) {

        final int width = Gdx.graphics.getWidth();
        final int height = Gdx.graphics.getHeight();
        final float CameraPixel;

        final float targetRatio = (float) width / (float) height;
        final float sourceRatio = pCamera.viewportWidth / pCamera.viewportHeight;

        if (targetRatio > sourceRatio) { CameraPixel = height / pCamera.viewportHeight; }
        else { CameraPixel = width / pCamera.viewportWidth; }

        cameraPixel = (int) CameraPixel;
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
