package no.erlyberly.bootlegterraria.util.aabb;

public class AABB1D extends AABB<AABB1D> {

    final int min;
    final int max;

    public AABB1D(int min, int max) {

        this.min = min;
        this.max = max;
    }

    @Override
    public boolean overlap(AABB1D other) {
        return this.max >= other.min && other.max >= this.min;
    }

    @Override
    public String toString() {
        return "AABB1D{" + "min=" + this.min + ", max=" + this.max + '}';
    }
}
