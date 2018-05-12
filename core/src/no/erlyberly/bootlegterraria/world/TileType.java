package no.erlyberly.bootlegterraria.world;

import java.util.HashMap;

public enum TileType {

    GRASS(1, true, "Grass"),
    DIRT(2, true, "Dirt"),
    SKY(3, false, "Sky"),
    LAVA(4, true, "Lava", 1),
    CLOUD(5, true, "Cloud"),
    STONE(6, true, "Stone");

    public static final int TILE_SIZE = 16;
    private static HashMap<Integer, TileType> tileMap;

    static {
        tileMap = new HashMap<Integer, TileType>();
        for (TileType tileType : TileType.values()) {
            tileMap.put(tileType.getId(), tileType);
        }
    }

    private int id;
    private boolean collidable;
    private String name;
    private int damage;

    TileType(int id, boolean collidable, String name) {
        this(id, collidable, name, 0);
    }

    TileType(int id, boolean collidable, String name, int damage) {
        this.id = id;
        this.collidable = collidable;
        this.name = name;
        this.damage = damage;
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

    public int getDamage() {
        return damage;
    }
}
