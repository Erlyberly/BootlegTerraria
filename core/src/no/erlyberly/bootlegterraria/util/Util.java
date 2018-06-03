package no.erlyberly.bootlegterraria.util;

import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.util.aabb.AABB2D;

public class Util {

    /**
     * Based on https://gist.github.com/steen919/8a079f4dadf88d4197bb/d732449eb74321207b4b189a3bcbf47a83c5db65
     * Converts the given hex color in 0xAARRGGBB format to a {@link Color} that can be used in a LibGdx application
     */
    public static Color convert(String str) {
        long hex = Long.decode(str);
        float a = (hex & 0xFF000000L) >> 24;
        float r = (hex & 0x00FF0000L) >> 16;
        float g = (hex & 0x0000FF00L) >> 8;
        float b = (hex & 0x000000FFL);
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
     * @return {@code val} if between {@code min} and {@code max}, if not return {@code min} or {@code max} respectivly
     */
    public static <T extends Comparable<T>> T between(T min, T val, T max) {
        Preconditions.checkNotNull(min, "None of the parameters can be null");
        Preconditions.checkNotNull(val, "None of the parameters can be null");
        Preconditions.checkNotNull(max, "None of the parameters can be null");
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
    public static <T extends Comparable<T>> boolean isBetween(T min, T val, T max) {
        Preconditions.checkNotNull(min, "None of the parameters can be null");
        Preconditions.checkNotNull(val, "None of the parameters can be null");
        Preconditions.checkNotNull(max, "None of the parameters can be null");
        Preconditions.checkArgument(min.compareTo(max) <= 0,
                                    "Minimum argument must be less than or equal to the maximum argument");
        if (val.compareTo(min) < 0) { return false; }
        else { return val.compareTo(max) < 0; }
    }

    public static AABB2D fromLight(Vector2Int src, LightLevel lightLevel) {

        final int lightRadius = lightLevel.getLvl() - 1;

        int blockX = src.x;
        int blockY = src.y;

        int mapWidth = (int) GameMain.inst().getGameMap().getWidth();
        int mapHeight = (int) GameMain.inst().getGameMap().getHeight();

        //start coords, top right
        final int x0 = Util.between(0, blockX - lightRadius, mapWidth);
        final int y0 = Util.between(0, blockY - lightRadius, mapHeight);

        //end coords, lower left
        final int x1 = Util.between(0, blockX + lightRadius, mapWidth);
        final int y1 = Util.between(0, blockY + lightRadius, mapHeight);
        return new AABB2D(x0, x1, y0, y1);
    }
}
