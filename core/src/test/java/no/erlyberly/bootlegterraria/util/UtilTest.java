package no.erlyberly.bootlegterraria.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void between() {
        Assertions.assertEquals(new Integer(0), Util.clamp(0, -1, 1)); //check less than min
        assertEquals(new Integer(1), Util.clamp(1, 0, 2)); //check between
        assertEquals(new Integer(1), Util.clamp(0, 2, 1)); //check max
        assertEquals(new Integer(0), Util.clamp(0, 0, 0)); //check max == min

        //make sure min must less than or equal to max
        assertThrows(IllegalArgumentException.class, () -> Util.clamp(2, 0, 0));
    }

    @Test
    void isBetween() {
        assertTrue(Util.isBetween(0, 1, 2));
        assertFalse(Util.isBetween(0, -1, 2));
        assertFalse(Util.isBetween(0, 3, 2));

        assertTrue(Util.isBetween(0, 0, 2));
        assertFalse(Util.isBetween(0, 2, 2));

        final int width = 16;
        int x;
        for (int i = 0; i < 1000; i++) {
            x = (int) (Math.random() * 16);
            assertEquals((x < 0 || x >= width), !Util.isBetween(0, x, width), "x=" + x);
        }

    }
}