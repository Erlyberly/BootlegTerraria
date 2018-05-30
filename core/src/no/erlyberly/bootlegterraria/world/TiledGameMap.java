package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.strongjoshua.console.LogLevel;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.entities.Player;
import no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer;
import no.erlyberly.bootlegterraria.util.LightLevel;
import no.erlyberly.bootlegterraria.util.Util;

public class TiledGameMap extends GameMap {

    private final TiledMap tiledMap;
    private final SimpleOrthogonalTiledMapRenderer tiledMapRenderer;
    private final TiledMapTileLayer blockLayer;

    private final Vector2 spawn;

    private final int mapWidth;
    private final int mapHeight;

    private final int tileWidth;
    private final int tileHeight;


    public TiledGameMap(final String map) {
        try {
            this.tiledMap = new TmxMapLoader().load(map);
        } catch (final Exception e) {
            GameMain.getConsoleHandler().logf("Failed to load map '%s'", LogLevel.ERROR, map);
            throw new IllegalArgumentException("Invalid map");
        }

        MapProperties mapProperties = this.tiledMap.getProperties();

        this.tileWidth = mapProperties.get("tilewidth", int.class);
        this.tileHeight = mapProperties.get("tileheight", int.class);

        this.mapWidth = mapProperties.get("width", int.class);
        this.mapHeight = mapProperties.get("height", int.class);

        int spawnX = mapProperties.get("spawnX", 0, int.class);
        int spawnY = mapProperties.get("spawnY", 0, int.class);
        this.spawn = blockToPixel(spawnX, spawnY);

        this.blockLayer = (TiledMapTileLayer) this.tiledMap.getLayers().get("blocks");

        this.tiledMapRenderer = new SimpleOrthogonalTiledMapRenderer(this.tiledMap, GameMain.TEST);
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
    public TileType getTileTypeByCoordinate(final MapLayer layer, final int col, final int row) {
        final Cell cell = ((TiledMapTileLayer) layer).getCell(col, row);

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
    public Vector2 getSpawn() {
        return this.spawn;
    }

    @Override
    public void spawnPlayer() {
        setPlayer(new Player(this.spawn.x, this.spawn.y));
    }

    @Override
    public void setBlockAt(final int blockX, final int blockY, final TileType tt) {
        final TileType oldID = TileType.getTileTypeByCell(this.blockLayer.getCell(blockX, blockY));
        if (oldID == tt || isOutsideMap(blockX, blockY)) {
            return;
        }
        Cell cell = null;
        if (tt != null) {
            cell = new Cell().setTile(this.tiledMap.getTileSets().getTile(tt.getId()));
        }
        this.blockLayer.setCell(blockX, blockY, cell);

        //Only update light when the block below the skylight is changed or if tile that emit light is placed or removed
        if (getSkylightAt(blockX) < blockY || (tt != null && tt.isEmittingLight()) ||
            (oldID != null && oldID.isEmittingLight())) {
            this.tiledMapRenderer.asyncUpdateLightAt(blockX);
        }
    }

    @Override
    public float getWidth() {
        return this.mapWidth;
    }

    @Override
    public float getHeight() {
        return this.mapHeight;
    }

    @Override
    public float getTileWidth() {
        return this.tileWidth;
    }

    @Override
    public float getTileHeight() {
        return this.tileHeight;
    }


    @Override
    public TiledMapTileLayer getBlockLayer() {
        return this.blockLayer;
    }

    @Override
    public Map getMap() {
        return this.tiledMap;
    }

    @Override
    public int getSkylightAt(int blockX) {
        if (!Util.isBetween(0, blockX, (int) getWidth())) {
            throw new IllegalArgumentException("No info about the outside of the map, x = " + blockX);
        }
        return this.tiledMapRenderer.getSkyLights()[blockX];
    }

    @Override
    public void calculateLightAt(int blockX, int y) {
        this.tiledMapRenderer.calculateLightAt(blockX, y, LightLevel.LVL_7);
    }

    @Override
    public void recalculateLight() {
        this.tiledMapRenderer.asyncUpdateLights();
    }

    @Override
    public LightLevel lightAt(int blockX, int blockY) {
        if (isOutsideMap(blockX, blockY)) {
            return null;
        }
        return this.tiledMapRenderer.lightAt(blockX, blockY);
    }
}
