package no.erlyberly.bootlegterraria.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.NumberUtils;

/**
 * Immutable two dimensional vector in an integer space
 */
public class Vector2Int implements Comparable<Vector2Int> {

    public final int x;
    public final int y;

    public Vector2Int(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return The length of this vector squared
     */
    public int length2() {
        return x * x + y * y;
    }

    /**
     * @return True length of this vector
     */
    public double length() {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * @return The distance between this vector and another
     */
    public double distLength(final Vector2Int v) {
        return dist(v).length();
    }

    /**
     * @return The distance between this vector and another
     */
    public Vector2Int dist(final Vector2Int v) {
        return new Vector2Int(v.x - x, v.y - y);
    }

    public static Vector2Int fromV2(final Vector2 v2) {
        return new Vector2Int((int) v2.x, (int) v2.y);
    }

    @Override
    public int compareTo(final Vector2Int o) {
        if (x == o.x) {
            return y - o.y;
        }
        return x - o.x;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + NumberUtils.floatToIntBits(x);
        result = prime * result + NumberUtils.floatToIntBits(y);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        final Vector2Int ov = (Vector2Int) o;
        return x == ov.x && y == ov.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ')';
    }
}
