package no.erlyberly.bootlegterraria.util.aabb;

/**
 * An implementation of AABB which only is checking for overlapping in a single dimension. This can be visualized by
 * drawing two lines above each other and looking
 */
public class AABB1D extends AABB<AABB1D> {

    final int min;
    final int max;

    public AABB1D(final int min, final int max) {

        this.min = min;
        this.max = max;
    }

    @Override
    public boolean overlap(final AABB1D other) {
        return this.max >= other.min && other.max >= this.min;
    }

    @Override
    public String toString() {
        return "AABB1D{" + "min=" + this.min + ", max=" + this.max + '}';
    }
}
