package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.light.LightLevel;

import java.util.HashMap;

public enum TileType {

    GRASS(1, "Grass"),
    DIRT(2, "Dirt"),
    LAVA(3, "Lava", 2500, LightLevel.LVL_8, 0.5f, 64),
    CLOUD(4, "Cloud", 0, 1024),
    STONE(5, "Stone"),
    TORCH(6, "Torch", LightLevel.LVL_7, 0, 256),
    LOG(7, "Log"),
    LEAVES(8, "Leaves"),
    ;

    public static final float TILE_SIZE = 16;
    public static final int TILE_TYPES = TileType.values().length;

    private static final HashMap<Integer, TileType> tileMap;

    static {
        tileMap = new HashMap<>();
        for (final TileType tileType : TileType.values()) {
            tileMap.put(tileType.getId(), tileType);
        }
    }

    private final int id;
    private final String name;
    private final int dps;
    private final LightLevel luminosity;
    private final float viscosity;
    private final int stackSize;

    TileType(final int id, final String name) {
        this(id, name, 1, 128);
    }

    TileType(final int id, final String name, final float viscosity, final int stackSize) {
        this(id, name, LightLevel.LVL_0, viscosity, stackSize);
    }

    TileType(final int id, final String name, final LightLevel lightLevel, final float viscosity, final int stackSize) {
        this(id, name, 0, lightLevel, viscosity, stackSize);
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
     *     How easily living move through the tile, 0 is no resistance, 1 is solid
     * @param stackSize
     *     How many of this tile a stack can hold
     */
    TileType(final int id, final String name, final int dps, final LightLevel luminosity, final float viscosity,
             final int stackSize) {
        Preconditions.checkArgument(viscosity >= 0 && viscosity <= 1, "viscosity must be between 0 and 1");
        Preconditions.checkArgument(stackSize > 0, "The stack size must be greater than 0");
        Preconditions.checkArgument(stackSize != Integer.MAX_VALUE,
                                    "Max stack size cannot be greater than " + (Integer.MAX_VALUE - 1) +
                                    " (Integer.MAX_VALUE - 1), given " + stackSize);
        this.id = id;
        this.name = name;
        this.dps = dps;
        this.luminosity = luminosity;
        this.viscosity = viscosity;
        this.stackSize = stackSize;
    }


    /**
     * @param id
     *     The Tiled ID of the {@code TileType} to get
     *
     * @return The {@code TileType} of the {@code id} or {@code null} if none is found
     */
    public static TileType getTileTypeById(final int id) {
        return tileMap.get(id);
    }

    /**
     * @param cell
     *     The cell to get the {@code TileType} of
     *
     * @return The {@code TileType} of the given {@code cell} or {@code null} if the cell, the cell's tile or invalid id
     * of cell's tile
     */
    public static TileType getTileTypeByCell(final TiledMapTileLayer.Cell cell) {
        if (cell == null || cell.getTile() == null) {
            return null;
        }
        return getTileTypeById(cell.getTile().getId());
    }

    public int getId() {
        return id;
    }

    public boolean isSolid() {
        return viscosity > 0;
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

    /**
     * @return Wherever this tile emits lights onto other blocks
     */
    public boolean isEmittingLight() {
        return luminosity != LightLevel.LVL_0 && luminosity != LightLevel.LVL_1;
    }

    @Override
    public String toString() {
        return name;
    }

    public int getMaxStackSize() {
        return stackSize;
    }

    public TextureRegion getTextureRegion() {
        return GameMain.map.getTileSets().getTile(id).getTextureRegion();
    }
}
