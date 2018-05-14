package no.erlyberly.bootlegterraria.world;

import no.erlyberly.bootlegterraria.helpers.LightLevel;

import java.util.HashMap;

public enum TileType {

    GRASS(1, true, "Grass"),
    DIRT(2, true, "Dirt"),
    SKY(3, false, "Sky"),
    LAVA(4, true, "Lava", 2500, LightLevel.LVL_7),
    CLOUD(5, false, "Cloud"),
    STONE(6, true, "Stone");

    public static final float TILE_SIZE = 16;
    private static HashMap<Integer, TileType> tileMap;

    static {
        tileMap = new HashMap<>();
        for (TileType tileType : TileType.values()) {
            tileMap.put(tileType.getId(), tileType);
        }
    }

    private int id;
    private boolean collidable;
    private String name;
    private int dps;
    private LightLevel luminosity;

    TileType(int id, boolean collidable, String name) {
        this(id, collidable, name, 0, LightLevel.LVL_0);
    }

    TileType(int id, boolean collidable, String name, int dps, LightLevel luminosity) {
        this.id = id;
        this.collidable = collidable;
        this.name = name;
        this.dps = dps;
        this.luminosity = luminosity;
    }


    public static TileType getTileTypeById(int id) {
        return tileMap.get(id);
    }

    public int getId() {
        return id;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public String getName() {
        return name;
    }

    public int getDps() {
        return dps;
    }

    public LightLevel getLuminosity() {
        return luminosity;
    }
}
