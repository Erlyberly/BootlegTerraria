package no.erlyberly.bootlegterraria.util.aabb;

import com.badlogic.gdx.math.Vector2;
import no.erlyberly.bootlegterraria.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    public Iterator<Vector2> iterator() {
        return new AABB2DIterator(this);
    }

    @Override
    public String toString() {
        return "AABB2D{" + "x=" + this.x + ", y=" + this.y + '}';
    }

    private class AABB2DIterator implements Iterator<Vector2> {

        private final List<Vector2> points;
        int i = 0;

        AABB2DIterator(AABB2D aabb2D) {
            this.points = new ArrayList<>();

            for (int x = aabb2D.x.min; x < aabb2D.x.max; x++) {
                for (int y = aabb2D.y.min; y < aabb2D.y.max; y++) {
                    this.points.add(new Vector2(x, y));
                }
            }
        }


        @Override
        public boolean hasNext() {
            return this.i < this.points.size();
        }

        @Override
        public Vector2 next() {
            return this.points.get(this.i++);
        }
    }
}
