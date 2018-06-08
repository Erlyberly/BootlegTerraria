package no.erlyberly.bootlegterraria.util;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Vector2IntTest {

    @Test
    void lengthSquared() {
        assertEquals(0, new Vector2Int(0, 0).length2());
        assertEquals(25, new Vector2Int(3, 4).length2());
        assertEquals(2, new Vector2Int(1, 1).length2());
        assertEquals(2, new Vector2Int(-1, -1).length2());
    }

    @Test
    void length() {
        assertEquals(0, new Vector2Int(0, 0).length());
        assertEquals(5, new Vector2Int(3, 4).length());
        assertEquals(Math.sqrt(2), new Vector2Int(1, 1).length());
        assertEquals(Math.sqrt(2), new Vector2Int(-1, -1).length());
        assertEquals(Math.sqrt(2), new Vector2Int(-1, 1).length());
    }

    @Test
    void distLength() {
        assertEquals(0, new Vector2Int(0, 0).distLength(new Vector2Int(0, 0)));
        assertEquals(5, new Vector2Int(0, 0).distLength(new Vector2Int(3, 4)));
        assertEquals(Math.sqrt(2), new Vector2Int(0, 0).distLength(new Vector2Int(1, 1)));
        assertEquals(5, new Vector2Int(-1, -2).distLength(new Vector2Int(2, 2)));
        assertEquals(5, new Vector2Int(3, 4).distLength(new Vector2Int(0, 0)));
    }

    @Test
    void dist() {
        assertEquals(new Vector2Int(0, 0), new Vector2Int(0, 0).dist(new Vector2Int(0, 0)));
        assertEquals(new Vector2Int(3, 4), new Vector2Int(0, 0).dist(new Vector2Int(3, 4)));
        assertEquals(new Vector2Int(-3, -4), new Vector2Int(3, 4).dist(new Vector2Int(0, 0)));
        assertEquals(new Vector2Int(3, 4), new Vector2Int(1, 1).dist(new Vector2Int(4, 5)));
        assertEquals(new Vector2Int(-3, -4), new Vector2Int(2, 3).dist(new Vector2Int(-1, -1)));
        assertEquals(new Vector2Int(0, 0), new Vector2Int(0, 0).dist(new Vector2Int(0, 0)));
    }

    @Test
    void compareTo() {
        final Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            final int x = random.nextInt();
            final int y = random.nextInt();

            final Vector2Int vorg = new Vector2Int(x, y);

            assertEquals(0, vorg.compareTo(vorg));

            assertEquals(-1, vorg.compareTo(new Vector2Int(x + 1, y)));
            assertEquals(1, vorg.compareTo(new Vector2Int(x - 1, y)));

            assertEquals(-1, vorg.compareTo(new Vector2Int(x, y + 1)));
            assertEquals(1, vorg.compareTo(new Vector2Int(x, y - 1)));

            assertEquals(-1, vorg.compareTo(new Vector2Int(x + 1, y + 1)));
            assertEquals(1, vorg.compareTo(new Vector2Int(x - 1, y - 1)));
        }
    }
}