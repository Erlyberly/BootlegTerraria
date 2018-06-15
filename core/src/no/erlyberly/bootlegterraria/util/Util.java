package no.erlyberly.bootlegterraria.util;

import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.util.aabb.AABB2D;
import no.erlyberly.bootlegterraria.world.GameMap;

public class Util {

    /**
     * Based on https://gist.github.com/steen919/8a079f4dadf88d4197bb/d732449eb74321207b4b189a3bcbf47a83c5db65
     * Converts the given hex color in 0xAARRGGBB format to a {@link Color} that can be used in a LibGdx application
     */
    public static Color convert(final String str) {
        final long hex = Long.decode(str);
        final float a = (hex & 0xFF000000L) >> 24;
        final float r = (hex & 0x00FF0000L) >> 16;
        final float g = (hex & 0x0000FF00L) >> 8;
        final float b = (hex & 0x000000FFL);
        return new Color(r / 255F, g / 255F, b / 255F, a / 255F);
    }

    /**
     * @param min
     *     The minimum value to return (inclusive)
     * @param val
     *     The value to verify is between {@code min} and {@code max}
     * @param max
     *     The maximum value to return (inclusive)
     *
     * @return {@code val} if between {@code min} and {@code max}, if not return {@code min} or {@code max} respectively
     */
    public static <T extends Comparable<T>> T clamp(final T min, final T val, final T max) {
        Preconditions.checkArgument(min != null && val != null && max != null, "None of the parameters can be null");
        Preconditions.checkArgument(min.compareTo(max) <= 0,
                                    "Minimum argument must be less than or equal to the maximum argument");
        if (val.compareTo(min) < 0) {
            return min;
        }
        else if (val.compareTo(max) > 0) {
            return max;
        }
        return val;
    }

    /**
     * @param min
     *     The minimum value to check (inclusive)
     * @param val
     *     The value to verify is between {@code min} and {@code max}
     * @param max
     *     The maximum value to check (exclusive)
     *
     * @return if {@code val} is between {@code min} (inclusive) and {@code max} (exclusive)
     */
    public static <T extends Comparable<T>> boolean isBetween(final T min, final T val, final T max) {
        Preconditions.checkArgument(min != null && val != null && max != null, "None of the parameters can be null");
        Preconditions.checkArgument(min.compareTo(max) <= 0,
                                    "Minimum argument must be less than or equal to the maximum argument");
        if (val.compareTo(min) < 0) { return false; }
        else { return val.compareTo(max) < 0; }
    }

    /**
     * Create an {@link AABB2D} at {@code src} so that this returns the Minimum AABB if a light with the brightness of
     * {@code lightLevel} is shining from {@code src}
     *
     * @return An AABB2D around {@code src} with a radius of lightLevel
     */
    public static AABB2D fromLight(final Vector2Int src, final GameMap gameMap, final LightLevel lightLevel) {

        final int lightRadius = lightLevel.getLvl() - 1;

        final int blockX = src.x;
        final int blockY = src.y;

        final int mapWidth = (int) gameMap.getWidth();
        final int mapHeight = (int) gameMap.getHeight();

        //start coords, top right
        final int x0 = Util.clamp(0, blockX - lightRadius, mapWidth);
        final int y0 = Util.clamp(0, blockY - lightRadius, mapHeight);

        //end coords, lower left
        final int x1 = Util.clamp(0, blockX + lightRadius + 1, mapWidth);
        final int y1 = Util.clamp(0, blockY + lightRadius + 1, mapHeight);
        return new AABB2D(x0, x1, y0, y1);
    }
}
