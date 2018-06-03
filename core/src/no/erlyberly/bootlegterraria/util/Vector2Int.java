package no.erlyberly.bootlegterraria.util;

import com.badlogic.gdx.utils.NumberUtils;

/**
 * Immutable two dimensional vector in an integer space
 */
public class Vector2Int implements Comparable<Vector2Int> {

    public final int x;
    public final int y;

    public Vector2Int(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return The length of this vector squared
     */
    public int length2() {
        return this.x * this.x + this.y * this.y;
    }

    /**
     * @return True length of this vector
     */
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * @return The distance between this vector and another
     */
    public double distLength(Vector2Int v) {
        return dist(v).length();
    }

    /**
     * @return The distance between this vector and another
     */
    public Vector2Int dist(Vector2Int v) {
        return new Vector2Int(v.x - this.x, v.y - this.y);
    }

    @Override
    public int compareTo(Vector2Int o) {
        if (this.x == o.x) {
            return this.y - o.y;
        }
        return this.x - o.x;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + NumberUtils.floatToIntBits(this.x);
        result = prime * result + NumberUtils.floatToIntBits(this.y);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Vector2Int ov = (Vector2Int) o;
        return this.x == ov.x && this.y == ov.y;
    }

    @Override
    public String toString() {
        return "Vector2Int{" + "x=" + this.x + ", y=" + this.y + '}';
    }
}
