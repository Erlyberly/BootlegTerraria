package no.erlyberly.bootlegterraria.util.aabb;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AABB1DTest {

    @Test
    void overlap() {
        AABB1D tst1 = new AABB1D(0, 10);
        AABB1D tst2 = new AABB1D(5, 15);
        AABB1D tst3 = new AABB1D(15, 18);

        assertTrue(tst1.overlap(tst2));
        assertTrue(tst2.overlap(tst1));

        assertFalse(tst1.overlap(tst3));
        assertFalse(tst3.overlap(tst1));

        assertFalse(tst1.overlap(tst3));
        assertFalse(tst3.overlap(tst1));
    }
}