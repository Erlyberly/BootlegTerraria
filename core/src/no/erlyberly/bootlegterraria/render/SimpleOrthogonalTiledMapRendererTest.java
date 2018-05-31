package no.erlyberly.bootlegterraria.render;

import org.junit.jupiter.api.Test;

public class SimpleOrthogonalTiledMapRendererTest {

    @Test
    void updateSkylightsAll(final SimpleOrthogonalTiledMapRenderer renderer) {
//        System.out.println(Arrays.toString(renderer.getSkyLights()));
//        renderer.updateLights();
//        System.out.println(Arrays.toString(renderer.getSkyLights()));
    }

    @Test
    void updateSkylightsAt(final SimpleOrthogonalTiledMapRenderer renderer) {
//        renderer.updateLightAt(0);
//        Assertions.assertEquals(5, renderer.getSkyLights()[0]);
//
//        renderer.updateLightAt(renderer.getSkyLights().length - 1);
//        Assertions.assertEquals(8, renderer.getSkyLights()[renderer.getSkyLights().length - 1]);
    }

    @Test
    void testLightSources(final SimpleOrthogonalTiledMapRenderer renderer) {
//        renderer.updateLights();
//        System.out.println("SkyLights: " + Arrays.toString(renderer.getSkyLights()));
//        HashMap<Vector2, LightLevel> expected = new HashMap<Vector2, LightLevel>() {{
//            put(new Vector2Int(0, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(0, 1), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(0, 2), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(0, 3), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(0, 4), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(0, 5), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(1, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(1, 1), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(1, 2), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(1, 3), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(1, 4), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(1, 5), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(1, 6), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(2, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(2, 1), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(2, 2), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(2, 3), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(2, 4), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(2, 5), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(2, 6), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(3, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(3, 1), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(3, 2), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(4, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(4, 1), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(5, 0), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(6, 0), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(7, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(7, 1), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(8, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(8, 1), SKY_LIGHT_BRIGHTNESS);
//            //
//            // none
//            //
//            put(new Vector2Int(10, 0), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(11, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(11, 1), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(11, 2), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(12, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(12, 1), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(12, 2), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(12, 3), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(12, 4), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(12, 5), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(12, 6), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(12, 7), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(13, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(13, 1), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(13, 2), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(13, 3), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(13, 4), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(13, 5), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(13, 6), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(13, 7), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(14, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(14, 1), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(14, 2), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(14, 3), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(14, 4), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(14, 5), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(14, 6), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(14, 7), SKY_LIGHT_BRIGHTNESS);
//            //
//            put(new Vector2Int(15, 0), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(15, 1), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(15, 2), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(15, 3), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(15, 4), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(15, 5), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(15, 6), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(15, 7), SKY_LIGHT_BRIGHTNESS);
//            put(new Vector2Int(15, 8), SKY_LIGHT_BRIGHTNESS);
//
//            //blocks that give out light
//
//            put(new Vector2Int(0, 8), TileType.LAVA.getLuminosity());
//            put(new Vector2Int(0, 9), TileType.LAVA.getLuminosity());
//            put(new Vector2Int(1, 9), TileType.LAVA.getLuminosity());
//
//        }};
//
//
//        HashMap<Vector2, LightLevel> actual = renderer.getLightSources();
//
//        System.out.println("actual = " + actual);
//
//        MapDifference<Vector2, LightLevel> diff = Maps.difference(expected, actual);
//        if (!diff.areEqual()) {
//            Assertions.fail("Only in actual map: " + diff.entriesOnlyOnRight() + "\nonly in expected map: " +
//                            diff.entriesOnlyOnLeft());
//        }
    }
}