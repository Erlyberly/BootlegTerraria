package no.erlyberly.bootlegterraria.render.light;

import no.erlyberly.bootlegterraria.util.Vector2Int;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LightInfoTest {

    @Test
    void multipleLights() {
        LightInfo li = new LightInfo(0, 0);

        li.put(new Vector2Int(1, -1), LightLevel.LVL_2);
        assertEquals(LightLevel.LVL_1, li.getLightLevel());
        li.put(new Vector2Int(1, 1), LightLevel.LVL_8);
        assertEquals(LightLevel.LVL_7, li.getLightLevel());
        li.put(new Vector2Int(-1, -1), LightLevel.LVL_8);
        assertEquals(LightLevel.LVL_7, li.getLightLevel());
        li.put(new Vector2Int(0, 0), LightLevel.LVL_8);
        assertEquals(LightLevel.LVL_8, li.getLightLevel());
    }

    @Test
    void singleLight() {

        LightInfo li = new LightInfo(0, 0);


        int x = (int) li.getPos().x;
        int y = (int) li.getPos().y;

        testPut(x, y, LightLevel.LVL_8, LightLevel.LVL_8, li);

        for (int i = LightLevel.LIGHT_LEVELS; i > 0; i--) {

            LightLevel emLL = LightLevel.valueOf(i - 1);
            int t = 1;
            for (int j = i - 1; j >= 0; j--) {
                LightLevel ll = LightLevel.valueOf(j - 1);
//                System.out.println("-");
                testPut(x + t, y, ll, emLL, li,
                        String.format("j:%d | emLL: %s | i:%d | ll:%s | (%d,%d)", j, emLL, i, ll, x + 1, y));
                testPut(x - t, y, ll, emLL, li,
                        String.format("j:%d | emLL: %s | i:%d | ll:%s | (%d,%d)", j, emLL, i, ll, x - 1, y));
                testPut(x, y + t, ll, emLL, li,
                        String.format("j:%d | emLL: %s | i:%d | ll:%s | (%d,%d)", j, emLL, i, ll, x, y + 1));
                testPut(x, y - t, ll, emLL, li,
                        String.format("j:%d | emLL: %s | i:%d | ll:%s | (%d,%d)", j, emLL, i, ll, x, y - 1));
                t++;
            }
        }

        int xmod = 1;
        int ymod = 1;
        for (int i = 0; i < 4; i++) {

            switch (i) {
//                case 0: xmod=ymod=1
                case 1:
                    xmod = -1;
                    ymod = 1;
                    break;
                case 2:
                    xmod = -1;
                    ymod = -1;
                    break;
                case 3:
                    xmod = 1;
                    ymod = -1;
                    break;
            }

            testPut(1 * xmod, 1 * ymod, LightLevel.LVL_7, LightLevel.LVL_8, li);
            testPut(2 * xmod, 2 * ymod, LightLevel.LVL_5, LightLevel.LVL_8, li);
            testPut(3 * xmod, 3 * ymod, LightLevel.LVL_4, LightLevel.LVL_8, li);
            testPut(4 * xmod, 4 * ymod, LightLevel.LVL_2, LightLevel.LVL_8, li);
            testPut(5 * xmod, 5 * ymod, LightLevel.LVL_1, LightLevel.LVL_8, li);
        }
    }

    private void testPut(int x, int y, LightLevel expected, LightLevel newLL, LightInfo li) {
        testPut(x, y, expected, newLL, li, "");
    }

    private void testPut(int x, int y, LightLevel expected, LightLevel newLL, LightInfo li, String err) {
//        System.out.println("---");
        Vector2Int v = new Vector2Int(x, y);
        li.put(v, newLL);
        assertEquals(expected, li.getLightLevel(), err);
        li.remove(v);
    }

    @Test
    void getCurrLL() {
        assertEquals(LightLevel.LVL_0, new LightInfo(0, 0).getLightLevel());
    }

    @Test
    void removeIfExist() {

        LightInfo li = new LightInfo(10, 10);

        Vector2Int v = new Vector2Int(10, 10);
        li.put(v, LightLevel.LVL_8);
        assertEquals(LightLevel.LVL_8, li.getLightLevel());
        li.remove(v);
        assertEquals(LightLevel.LVL_0, li.getLightLevel());

        li.put(new Vector2Int(10, 10), LightLevel.LVL_8);
        assertEquals(LightLevel.LVL_8, li.getLightLevel());
        li.remove(new Vector2Int(10, 10));
        assertEquals(LightLevel.LVL_0, li.getLightLevel());

        li.put(new Vector2Int(10, 10), LightLevel.LVL_8);
        assertEquals(LightLevel.LVL_8, li.getLightLevel());
        li.put(new Vector2Int(10, 10), LightLevel.LVL_0);
        assertEquals(LightLevel.LVL_0, li.getLightLevel());
    }
}