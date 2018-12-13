package no.erlyberly.bootlegterraria.util.aabb;

import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.util.Vector2Int;

import java.util.Iterator;

/**
 * AABB2D is the implementation that should be used in a two dimensional space. This implementation can check if a
 * vector is within the AABB (with {@link #hasPoint(Vector2Int)} and loop trough all points within the AABB (with the
 * iterator)
 */
public class AABB2D extends AABB<AABB2D> implements Iterable<Vector2Int> {


    private final AABB1D x;
    private final AABB1D y;

    public AABB2D(final int minX, final int maxX, final int minY, final int maxY) {
        x = new AABB1D(minX, maxX);
        y = new AABB1D(minY, maxY);
    }

    @Override
    public boolean overlap(final AABB2D other) {
        return x.overlap(other.x) && y.overlap(other.y);
    }

    public boolean hasPoint(final Vector2Int p) {
        return Util.isBetween(x.min, p.x, x.max) && Util.isBetween(y.min, p.y, y.max);
    }

    @Override
    public String toString() {
        return "AABB2D{" + "x=" + x + ", y=" + y + '}';
    }

    @Override
    public Iterator<Vector2Int> iterator() {
        return new AABB2DIterator(this);
    }

    private class AABB2DIterator implements Iterator<Vector2Int> {

        private final int minX;
        private final int maxX;

        private final int minY;
        private final int maxY;

        int cX;
        int cY;

        AABB2DIterator(final AABB2D aabb) {
            minX = aabb.x.min;
            maxX = aabb.x.max;

            minY = aabb.y.min;
            maxY = aabb.y.max;

            cX = minX;
            cY = minY;
        }


        @Override
        public boolean hasNext() {
            return cY < maxY;
        }

        @Override
        public Vector2Int next() {
            final Vector2Int v = new Vector2Int(cX++, cY);
            if (cX >= maxX) {
                cY++;
                cX = minX;
            }
            return v;
        }
    }
}
