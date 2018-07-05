package no.erlyberly.bootlegterraria.util;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.input.MouseInput;
import no.erlyberly.bootlegterraria.render.light.LightLevel;
import no.erlyberly.bootlegterraria.util.aabb.AABB2D;
import no.erlyberly.bootlegterraria.world.GameMap;

import java.util.*;

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
        Preconditions.checkArgument(min.compareTo(max) <= 0, "Minimum argument (" + min +
                                                             ") must be less than or equal to the maximum argument(" +
                                                             max + ")");
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

    /**
     * <a href="https://stackoverflow.com/questions/1670862/obtaining-a-powerset-of-a-set-in-java#1670871">original
     * found here</a>
     *
     * @return The power set of a set
     */
    public static <T> Set<Set<T>> powerSet(final Set<T> originalSet) {
        final Set<Set<T>> sets = new HashSet<>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<>());
            return sets;
        }
        final List<T> list = new ArrayList<>(originalSet);
        final T head = list.get(0);
        final Set<T> rest = new HashSet<>(list.subList(1, list.size()));
        for (final Set<T> set : powerSet(rest)) {
            final Set<T> newSet = new HashSet<>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }

    public static String keysToString(final Integer[] keys) {
        final StringBuilder keyName = new StringBuilder("[");

        for (final int keycode : keys) {
            final String name;
            if (keycode > 255) {
                name = MouseInput.toString(keycode);
            }
            else {
                name = Input.Keys.toString(keycode);
            }

            keyName.append(name);
            keyName.append("(").append(keycode).append(") ");
        }

        //remove the last space
        keyName.setLength(keyName.length() - 1);

        return keyName.append("]").toString();
    }

    /**
     * @param string
     *     The string to convert to title case
     *
     * @return The title case version of the given string
     */
    public static String toTitleCase(final String string) {
        final StringBuilder sb = new StringBuilder();

        final String ACTIONABLE_DELIMITERS = " '-/";
        boolean capNext = true;

        for (char c : string.toCharArray()) {
            c = (capNext) ? Character.toTitleCase(c) : Character.toLowerCase(c);
            sb.append(c);
            capNext = ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0;
        }

        return sb.toString();
    }

    public static Map<String, String> interpreterArgs(final String[] args) {
        final HashMap<String, String> argsMap = new HashMap<>();

        for (final String arg : args) {
            if (!arg.startsWith("-")) {
                System.err.printf("Unknown argument '%s'", arg);
            }
            else {
                //we only care about the first equals sign, the rest is a part of the value
                final int equal = arg.indexOf('=');

                //if there is no equal sign there is no value
                if (equal == -1) {
                    //do not include the dash
                    argsMap.put(arg.substring(1), null);
                }
                else {
                    //find the key and value from the index of the first equal sign, but do not include it in the
                    // key or value
                    final String key = arg.substring(1, equal);
                    final String val = arg.substring(equal + 1, arg.length());
                    argsMap.put(key, val);
                }
            }
        }
//        System.out.println("argsMap.keySet() = " + argsMap.keySet());
//        System.out.println("argsMap.values() = " + argsMap.values());
        return argsMap;
    }
}
