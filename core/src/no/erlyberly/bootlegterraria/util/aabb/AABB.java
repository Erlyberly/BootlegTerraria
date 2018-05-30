package no.erlyberly.bootlegterraria.util.aabb;

public abstract class AABB<T extends AABB> {

    /**
     * @param other
     *     The other AABB to test for overlapping
     *
     * @return If this overlaps with the other AABB
     */
    public abstract boolean overlap(T other);
}
