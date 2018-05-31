package no.erlyberly.bootlegterraria.render.Light.api;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Vector2IntTest {


    @Test
    void compareTo() {
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            int x = random.nextInt();
            int y = random.nextInt();

            Vector2Int vorg = new Vector2Int(x, y);

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