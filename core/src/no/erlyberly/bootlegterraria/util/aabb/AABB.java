package no.erlyberly.bootlegterraria.util.aabb;

/**
 * <a href="https://en.wikipedia.org/wiki/Minimum_bounding_box">AABB (Axis Aligned Bounding Box)</a> is here used to
 * check if two boxes overlap each other.
 */
public abstract class AABB<T extends AABB> {

    /**
     * @param other
     *     The other AABB to test for overlapping
     *
     * @return If this overlaps with the other AABB
     */
    public abstract boolean overlap(T other);
}
