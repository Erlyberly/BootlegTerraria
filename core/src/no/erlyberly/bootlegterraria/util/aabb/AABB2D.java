package no.erlyberly.bootlegterraria.util.aabb;

import com.badlogic.gdx.math.Vector2;
import no.erlyberly.bootlegterraria.util.Util;

import java.util.Iterator;

/**
 * AABB2D is the implementation that should be used in a two dimensional space. This implementation can check if a
 * vector is within the AABB (with {@link #hasPoint(Vector2)} and loop trough all points within the AABB (with the
 * iterator)
 */
public class AABB2D extends AABB<AABB2D> implements Iterable<Vector2> {


    private final AABB1D x;
    private final AABB1D y;

    public AABB2D(int minX, int maxX, int minY, int maxY) {
        this.x = new AABB1D(minX, maxX);
        this.y = new AABB1D(minY, maxY);
    }

    @Override
    public boolean overlap(AABB2D other) {
        return this.x.overlap(other.x) && this.y.overlap(other.y);
    }

    public boolean hasPoint(Vector2 p) {
        return Util.isBetween(this.x.min, (int) p.x, this.x.max) && Util.isBetween(this.y.min, (int) p.y, this.y.max);
    }

    @Override
    public String toString() {
        return "AABB2D{" + "x=" + this.x + ", y=" + this.y + '}';
    }

    @Override
    public Iterator<Vector2> iterator() {
        return new AABB2DIterator(this);
    }

    private class AABB2DIterator implements Iterator<Vector2> {

        private final int minX;
        private final int maxX;

        private final int minY;
        private final int maxY;

        int cX;
        int cY;

        AABB2DIterator(AABB2D aabb) {
            this.minX = aabb.x.min;
            this.maxX = aabb.x.max;

            this.minY = aabb.y.min;
            this.maxY = aabb.y.max;

            this.cX = this.minX;
            this.cY = this.minY;
        }


        @Override
        public boolean hasNext() {
            return this.cY < this.maxY;
        }

        @Override
        public Vector2 next() {
            Vector2 v = new Vector2(this.cX++, this.cY);
            if (this.cX >= this.maxX) {
                this.cY++;
                this.cX = this.minX;
            }
            return v;
        }
    }
}
