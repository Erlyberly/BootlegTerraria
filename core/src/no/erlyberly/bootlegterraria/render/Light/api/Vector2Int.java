package no.erlyberly.bootlegterraria.render.Light.api;

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

    @Override
    public int compareTo(Vector2Int o) {
        if (this.x == o.x) {
            return this.y - o.y;
        }
        return this.x - o.x;
    }

    @Override
    public int hashCode() {
        return this.x * 31 + this.y * 43;
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
