package no.erlyberly.bootlegterraria.render.light;

import no.erlyberly.bootlegterraria.util.Vector2Int;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LightInfoTest {

    @Test
    void multipleLights() {
        final LightInfo li = new LightInfo(0, 0);

        li.put(new Vector2Int(1, -1), LightLevel.LVL_2, false);
        assertEquals(LightLevel.LVL_1, li.getLightLevel());
        li.put(new Vector2Int(1, 1), LightLevel.LVL_8, false);
        assertEquals(LightLevel.LVL_7, li.getLightLevel());
        li.put(new Vector2Int(-1, -1), LightLevel.LVL_8, false);
        assertEquals(LightLevel.LVL_7, li.getLightLevel());
        li.put(new Vector2Int(0, 0), LightLevel.LVL_8, false);
        assertEquals(LightLevel.LVL_8, li.getLightLevel());
    }

    @Test
    void singleLight() {

        final LightInfo li = new LightInfo(0, 0);


        final int x = (int) li.getPosf().x;
        final int y = (int) li.getPosf().y;

        testPut(x, y, LightLevel.LVL_8, LightLevel.LVL_8, li);

        for (int i = LightLevel.LIGHT_LEVELS; i > 0; i--) {

            final LightLevel emLL = LightLevel.valueOf(i - 1);
            int t = 1;
            for (int j = i - 1; j >= 0; j--) {
                final LightLevel ll = LightLevel.valueOf(j - 1);
//                System.out.println("-");
                testPut(x + t, y, ll, emLL, li, false,
                        String.format("j:%d | emLL: %s | i:%d | ll:%s | (%d,%d)", j, emLL, i, ll, x + 1, y));
                testPut(x - t, y, ll, emLL, li, false,
                        String.format("j:%d | emLL: %s | i:%d | ll:%s | (%d,%d)", j, emLL, i, ll, x - 1, y));
                testPut(x, y + t, ll, emLL, li, false,
                        String.format("j:%d | emLL: %s | i:%d | ll:%s | (%d,%d)", j, emLL, i, ll, x, y + 1));
                testPut(x, y - t, ll, emLL, li, false,
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

    private void testPut(final int x, final int y, final LightLevel expected, final LightLevel newLL,
                         final LightInfo li) {
        testPut(x, y, expected, newLL, li, false, "");
    }

    private void testPut(final int x, final int y, final LightLevel expected, final LightLevel newLL,
                         final LightInfo li, final boolean skylight, final String err) {
//        System.out.println("---");
        final Vector2Int v = new Vector2Int(x, y);
        li.put(v, newLL, skylight);
        assertEquals(expected, li.getLightLevel(), err);
        li.remove(v, skylight);
    }

    @Test
    void getCurrLL() {
        assertEquals(LightLevel.LVL_0, new LightInfo(0, 0).getLightLevel());
    }

    @Test
    void removeIfExist() {

        final LightInfo li = new LightInfo(10, 10);

        final Vector2Int v = new Vector2Int(10, 10);
        li.put(v, LightLevel.LVL_8, false);
        assertEquals(LightLevel.LVL_8, li.getLightLevel());
        li.remove(v, false);
        assertEquals(LightLevel.LVL_0, li.getLightLevel());

        li.put(new Vector2Int(10, 10), LightLevel.LVL_8, false);
        assertEquals(LightLevel.LVL_8, li.getLightLevel());
        li.remove(new Vector2Int(10, 10), false);
        assertEquals(LightLevel.LVL_0, li.getLightLevel());

        li.put(new Vector2Int(10, 10), LightLevel.LVL_8, false);
        assertEquals(LightLevel.LVL_8, li.getLightLevel());
        li.put(new Vector2Int(10, 10), LightLevel.LVL_0, false);
        assertEquals(LightLevel.LVL_0, li.getLightLevel());
    }

    @Test
    void skylight() {

        final Vector2Int src00 = new Vector2Int(0, 0);
        final Vector2Int src10 = new Vector2Int(1, 0);
        final LightInfo li = new LightInfo(src00);

        li.put(src00, LightLevel.LVL_8, true);
        li.put(src00, LightLevel.LVL_7, false);
        li.put(src10, LightLevel.LVL_3, false); //test light

        assertTrue(li.isSkylight());
        assertEquals(LightLevel.LVL_8, li.getLightLevel());

        li.remove(src00, true);
        li.put(src00, li.getEmitting(), false); //simulate putting on the real light
        assertFalse(li.isSkylight());
        assertEquals(LightLevel.LVL_7, li.getLightLevel());

        li.remove(src00, true);
        assertFalse(li.isSkylight());
        assertEquals(LightLevel.LVL_7, li.getLightLevel());

        li.remove(src00, false);
        assertEquals(LightLevel.LVL_2, li.getLightLevel());
    }

    @Test
    void skylight2() {

        final Vector2Int src00 = new Vector2Int(0, 0);
        final Vector2Int src10 = new Vector2Int(1, 0);
        final LightInfo li = new LightInfo(src00);

        li.put(src00, LightLevel.LVL_7, false);
        li.put(src00, LightLevel.LVL_8, true);
        li.put(src10, LightLevel.LVL_3, false); //test light

        assertTrue(li.isSkylight());
        assertEquals(LightLevel.LVL_8, li.getLightLevel());

        li.remove(src00, true);
        li.put(src00, li.getEmitting(), false);
        assertFalse(li.isSkylight());
        assertEquals(LightLevel.LVL_7, li.getLightLevel());

        li.remove(src00, false);
        assertEquals(LightLevel.LVL_2, li.getLightLevel());
    }


    @Test
    void skylight3() {
        final Vector2Int src00 = new Vector2Int(0, 0);
        final Vector2Int src01 = new Vector2Int(0, 1);

        final LightInfo li = new LightInfo(src00);
        final LightInfo liUp = new LightInfo(src01);

        liUp.put(src00, LightLevel.SKY_LIGHT, true);
        li.put(src00, LightLevel.SKY_LIGHT, true);

        liUp.put(src01, LightLevel.SKY_LIGHT, true);
        li.put(src01, LightLevel.SKY_LIGHT, true);

        final int i = 0;
        li.remove(src00, true);

        assertFalse(li.isSkylight());
    }
}