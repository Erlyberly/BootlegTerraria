package no.erlyberly.bootlegterraria.render.light.api;

import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.util.Vector2Int;

/**
 * An interface to hold the light data of a map.
 */
public interface LightMap {

    /**
     * @param blockX
     *     The x-coordinate of the block
     * @param blockY
     *     The x-coordinate of the block
     *
     * @return The LightLevel at ({@code blockX, blockY})
     */
    default LightLevel lightAt(int blockX, int blockY) {
        return lightAt(new Vector2Int(blockX, blockY));
    }

    /**
     * @param pos
     *     The coordinate of the block
     *
     * @return The LightLevel at ({@code blockX, blockY})
     */
    LightLevel lightAt(Vector2Int pos);


    /**
     * @param blockX
     *     The x-coordinate of the block
     * @param blockY
     *     The x-coordinate of the block
     *     <p>
     *     Put/remove a light at given position
     */
    default void put(int blockX, int blockY, LightLevel ll) {
        put(new Vector2Int(blockX, blockY), ll);
    }

    /**
     * @param pos
     *     The coordinate of the block
     *     Put/remove a light at given position
     */
    void put(Vector2Int pos, LightLevel ll);

    void remove(Vector2Int pos);

    default void remove(int blockX, int blockY) {
        remove(new Vector2Int(blockX, blockY));
    }

    /**
     * @return A copy of the blocking light positions
     */
    int[] getSkylight();

}
