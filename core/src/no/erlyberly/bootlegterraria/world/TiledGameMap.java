package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.strongjoshua.console.LogLevel;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.Player;
import no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer;

public class TiledGameMap extends GameMap {

    private TiledMap tiledMap;
    private final SimpleOrthogonalTiledMapRenderer tiledMapRenderer;
    private final TiledMapTileLayer blockLayer;

    private final int spawnX;
    private final int spawnY;

    public TiledGameMap(final String map) {
        try {
            this.tiledMap = new TmxMapLoader().load(map);
        } catch (final Exception e) {
            GameMain.getConsoleHandler().log("Failed to load map '%s'", LogLevel.ERROR, map);
        }

        this.spawnX = (int) (this.tiledMap.getProperties().get("spawnX", 0, Integer.class) * TileType.TILE_SIZE);
        this.spawnY =
            (int) ((getHeight() - this.tiledMap.getProperties().get("spawnY", 0, Integer.class)) * TileType.TILE_SIZE);

        this.blockLayer = (TiledMapTileLayer) this.tiledMap.getLayers().get("blocks");

        System.out.println("spawnX = " + this.spawnX);
        System.out.println("spawnY = " + this.spawnY);

        this.tiledMapRenderer = new SimpleOrthogonalTiledMapRenderer(this.tiledMap, GameMain.TEST);

        setPlayer(new Player(getSpawnX(), getSpawnY(), this));
    }

    @Override
    public void render(final OrthographicCamera camera, final OrthographicCamera hudCamera, final SpriteBatch batch) {
        batch.setProjectionMatrix(camera.combined);
        this.tiledMapRenderer.setView(camera);

        this.tiledMapRenderer.render();

        super.render(camera, hudCamera, batch);
    }

    @Override
    public void dispose() {
        this.tiledMap.dispose();
    }

    @Override
    public TileType getTileTypeByCoordinate(final int layer, final int col, final int row) {
        final Cell cell = ((TiledMapTileLayer) this.tiledMap.getLayers().get(layer)).getCell(col, row);

        if (cell != null) {
            final TiledMapTile tile = cell.getTile();

            if (tile != null) {
                final int id = tile.getId();
                return TileType.getTileTypeById(id);
            }
        }
        return null;
    }

    @Override
    public int getLayers() {
        return this.tiledMap.getLayers().getCount();
    }

    @Override
    public float getWidth() {
        return ((TiledMapTileLayer) this.tiledMap.getLayers().get(0)).getWidth();
    }

    @Override
    public float getHeight() {
        return ((TiledMapTileLayer) this.tiledMap.getLayers().get(0)).getHeight();
    }

    @Override
    public int getSpawnX() {
        return this.spawnX;
    }

    @Override
    public int getSpawnY() {
        return this.spawnY;
    }

    @Override
    public void setBlockAt(final int x, final int y, final TileType tt) {
        Cell cell = null;
        if (tt != null) {
            cell = new Cell().setTile(this.tiledMap.getTileSets().getTile(tt.getId()));
        }

        this.blockLayer.setCell(x, y, cell);
        this.tiledMapRenderer.asyncUpdateLightAt(x);
    }
}
