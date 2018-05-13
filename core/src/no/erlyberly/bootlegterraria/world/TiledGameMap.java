package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.strongjoshua.console.LogLevel;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.Player;
import no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer;

public class TiledGameMap extends GameMap {

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;

    private final int spawnX;
    private final int spawnY;

    public TiledGameMap(String map) {
        try {
            this.tiledMap = new TmxMapLoader().load(map);
        } catch (Exception e) {
            GameMain.getConsoleHandler().log("Failed to load map '%s'", LogLevel.ERROR, map);
        }

        spawnX = (int) (tiledMap.getProperties().get("spawnX", 0, Integer.class) * TileType.TILE_SIZE);
        spawnY = (int) ((getHeight() - tiledMap.getProperties().get("spawnY", 0, Integer.class)) * TileType.TILE_SIZE);

        System.out.println("spawnX = " + spawnX);
        System.out.println("spawnY = " + spawnY);

        this.tiledMapRenderer = new SimpleOrthogonalTiledMapRenderer(tiledMap, GameMain.TEST);

        setPlayer(new Player(getSpawnX(), getSpawnY(), this));
    }

    @Override
    public void render(OrthographicCamera camera, OrthographicCamera hudCamera, SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();

        super.render(camera, hudCamera, batch);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    @Override
    public void dispose() {
        tiledMap.dispose();
    }

    @Override
    public TileType getTileTypeByCoordinate(int layer, int col, int row) {
        TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) tiledMap.getLayers().get(layer)).getCell(col, row);

        if (cell != null) {
            TiledMapTile tile = cell.getTile();

            if (tile != null) {
                int id = tile.getId();
                return TileType.getTileTypeById(id);
            }
        }
        return null;
    }

    @Override
    public int getLayers() {
        return tiledMap.getLayers().getCount();
    }

    @Override
    public float getWidth() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth();
    }

    @Override
    public float getHeight() {
        return ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getHeight();
    }

    @Override
    public int getSpawnX() {
        return spawnX;
    }

    @Override
    public int getSpawnY() {
        return spawnY;
    }
}
