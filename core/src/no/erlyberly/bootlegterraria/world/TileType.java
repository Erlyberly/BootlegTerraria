package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import no.erlyberly.bootlegterraria.util.LightLevel;

import java.util.HashMap;

public enum TileType {

    GRASS(1, true, "Grass"),
    DIRT(2, true, "Dirt"),
    SKY(3, false, "Sky"),
    LAVA(4, true, "Lava", 2500, LightLevel.LVL_6),
    CLOUD(5, false, "Cloud"),
    STONE(6, true, "Stone"),
    TORCH(7, false, "Torch", LightLevel.LVL_6);

    public static final float TILE_SIZE = 16;
    private static final HashMap<Integer, TileType> tileMap;

    static {
        tileMap = new HashMap<>();
        for (TileType tileType : TileType.values()) {
            tileMap.put(tileType.getId(), tileType);
        }
    }

    private final int id;
    private final boolean collidable;
    private final String name;
    private final int dps;
    private final LightLevel luminosity;

    TileType(int id, boolean collidable, String name) {
        this(id, collidable, name, LightLevel.LVL_0);
    }

    TileType(int id, boolean collidable, String name, LightLevel lightLevel) {
        this(id, collidable, name, 0, lightLevel);
    }

    TileType(int id, boolean collidable, String name, int dps, LightLevel luminosity) {
        this.id = id;
        this.collidable = collidable;
        this.name = name;
        this.dps = dps;
        this.luminosity = luminosity;
    }


    public static TileType getTileTypeById(int id) {
        return tileMap.getOrDefault(id, null);
    }

    public static TileType getTileTypeByCell(TiledMapTileLayer.Cell cell) {
        if (cell == null) {
            return null;
        }
        return getTileTypeById(cell.getTile().getId());
    }

    public int getId() {
        return this.id;
    }

    public boolean isCollidable() {
        return this.collidable;
    }

    public String getName() {
        return this.name;
    }

    public int getDps() {
        return this.dps;
    }

    public LightLevel getLuminosity() {
        return this.luminosity;
    }

    /**
     * @return Wherever this tile emits lights onto other blocks
     */
    public boolean isEmittingLight() {
        return this.luminosity != LightLevel.LVL_0 && this.luminosity != LightLevel.LVL_1;
    }
}
