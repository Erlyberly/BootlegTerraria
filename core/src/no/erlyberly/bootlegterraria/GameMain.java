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

    @Override
    public void create() {
        batch = new SpriteBatch();

        consoleHandler = new ConsoleHandler(this);

        this.camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        this.hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera.update();

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
                System.out.println(
                    "Tile clicked: " + type.getName() + ", id: " + type.getId() + ", dmg: " + type.getDamage() +
                    " coord: " + (int) (pos.x / TileType.TILE_SIZE) + "," + (int) (pos.y / TileType.TILE_SIZE));
            }
            else {
                System.out.println("Not a tile");
            }

        }

        camera.update();
        gameMap.update(Gdx.graphics.getDeltaTime());
        gameMap.render(camera, hudCamera, batch);
        consoleHandler.draw();
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
