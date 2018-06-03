package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.render.light.LightLevel;

import java.util.HashMap;

public enum TileType {

    GRASS(1, "Grass"),
    DIRT(2, "Dirt"),
    LAVA(3, "Lava", 2500, LightLevel.LVL_6, 0.5f),
    CLOUD(4, "Cloud", 0),
    STONE(5, "Stone"),
    TORCH(6, "Torch", LightLevel.LVL_6, 0);

    public static final float TILE_SIZE = 16;
    public static final int TILE_TYPES = TileType.values().length;

    private static final HashMap<Integer, TileType> tileMap;

    static {
        tileMap = new HashMap<>();
        for (TileType tileType : TileType.values()) {
            tileMap.put(tileType.getId(), tileType);
        }
    }

    private final int id;
    private final String name;
    private final int dps;
    private final LightLevel luminosity;
    private final float viscosity;

    TileType(int id, String name) {
        this(id, name, 1);
    }

    TileType(int id, String name, float viscosity) {
        this(id, name, LightLevel.LVL_0, viscosity);
    }

    TileType(int id, String name, LightLevel lightLevel, float viscosity) {
        this(id, name, 0, lightLevel, viscosity);
    }

    /**
     * @param id
     *     Tiled tile ID of this tile
     * @param name
     *     Display name
     * @param dps
     *     Damage per second
     * @param luminosity
     *     How bright this tile shines
     * @param viscosity
     *     How easily entities move through the tile, 0 is no resistance, 1 is solid
     */
    TileType(int id, String name, int dps, LightLevel luminosity, float viscosity) {
        Preconditions.checkArgument(viscosity >= 0 && viscosity <= 1, "viscosity must be between 0 and 1");
        this.id = id;
        this.name = name;
        this.dps = dps;
        this.luminosity = luminosity;
        this.viscosity = viscosity;
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
        return this.viscosity > 0;
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

    @Override
    public String toString() {
        return this.name;
    }
}
