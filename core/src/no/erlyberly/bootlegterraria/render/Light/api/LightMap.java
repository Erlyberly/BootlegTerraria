package no.erlyberly.bootlegterraria.render.light.api;

import no.erlyberly.bootlegterraria.render.light.LightInfo;
import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.util.Loadable;
import no.erlyberly.bootlegterraria.util.Vector2Int;

/**
 * An interface to hold the light data of a map.
 */
public interface LightMap extends Loadable {

    /**
     * @param blockX
     *     The x-coordinate of the block
     * @param blockY
     *     The x-coordinate of the block
     *
     * @return The LightLevel at ({@code blockX, blockY})
     */
    default LightLevel lightAt(final int blockX, final int blockY) {
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
     * Add a source of light at given position
     *
     * @param blockX
     *     The x-coordinate of the block
     * @param blockY
     *     The y-coordinate of the block
     */
    default void addSource(final int blockX, final int blockY, final LightLevel ll) {
        addSource(new Vector2Int(blockX, blockY), ll);
    }

    /**
     * Add a source of light at given position
     *
     * @param pos
     *     The coordinates to put the light at
     */
    void addSource(Vector2Int pos, LightLevel ll);

    /**
     * Remove a light source at give coordinate, if there is no light shining from there nothing happens
     *
     * @param pos
     *     The coordinates to remove the light from
     */
    void removeSource(Vector2Int pos);

    /**
     * Remove a light source at give coordinate, if there is no light shining from there nothing happens
     *
     * @param blockX
     *     The x-coordinate of the block
     * @param blockY
     *     The x-coordinate of the block
     */
    default void removeSource(final int blockX, final int blockY) {
        removeSource(new Vector2Int(blockX, blockY));
    }

    /**
     * @return The skylight's position at {@code blockX}
     */
    int getSkylightAt(int blockX);

    /**
     * Force a recalculation of the skylight in a given column
     *
     * @param x
     *     The column to recalculate the skylight of
     */
    void calculateSkylight(int x);

    default LightInfo lightInfoAt(final int blockX, final int blockY) {
        return lightInfoAt(new Vector2Int(blockX, blockY));
    }

    /**
     * @return The light info on this position. A new light info will be created if there is no info here
     */
    LightInfo lightInfoAt(Vector2Int pos);
}
