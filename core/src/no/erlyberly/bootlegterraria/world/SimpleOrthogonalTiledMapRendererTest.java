package no.erlyberly.bootlegterraria.world;

import com.badlogic.gdx.math.Vector2;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import no.erlyberly.bootlegterraria.helpers.LightLevel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

import static no.erlyberly.bootlegterraria.world.SimpleOrthogonalTiledMapRenderer.SKY_LIGHT_BRIGHTNESS;

public class SimpleOrthogonalTiledMapRendererTest {

    @Test
    void updateSkylightsAll(SimpleOrthogonalTiledMapRenderer renderer) {
//        System.out.println(Arrays.toString(renderer.getSkyLights()));
        renderer.updateLights();
//        System.out.println(Arrays.toString(renderer.getSkyLights()));
    }

    @Test
    void updateSkylightsAt(SimpleOrthogonalTiledMapRenderer renderer) {
        renderer.updateLightAt(0);
        Assertions.assertEquals(5, renderer.getSkyLights()[0]);

        renderer.updateLightAt(renderer.getSkyLights().length - 1);
        Assertions.assertEquals(8, renderer.getSkyLights()[renderer.getSkyLights().length - 1]);
    }

    @Test
    void testLightSources(SimpleOrthogonalTiledMapRenderer renderer) {
        renderer.updateLights();
        renderer.updateLightSources();
        System.out.println("SkyLights: " + Arrays.toString(renderer.getSkyLights()));
        HashMap<Vector2, LightLevel> expected = new HashMap<Vector2, LightLevel>() {{
            put(new Vector2(0, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(0, 1), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(0, 2), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(0, 3), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(0, 4), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(0, 5), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(1, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(1, 1), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(1, 2), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(1, 3), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(1, 4), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(1, 5), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(1, 6), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(2, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(2, 1), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(2, 2), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(2, 3), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(2, 4), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(2, 5), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(2, 6), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(3, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(3, 1), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(3, 2), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(4, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(4, 1), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(5, 0), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(6, 0), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(7, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(7, 1), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(8, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(8, 1), SKY_LIGHT_BRIGHTNESS);
            //
            // none
            //
            put(new Vector2(10, 0), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(11, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(11, 1), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(11, 2), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(12, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(12, 1), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(12, 2), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(12, 3), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(12, 4), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(12, 5), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(12, 6), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(12, 7), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(13, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(13, 1), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(13, 2), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(13, 3), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(13, 4), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(13, 5), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(13, 6), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(13, 7), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(14, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(14, 1), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(14, 2), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(14, 3), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(14, 4), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(14, 5), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(14, 6), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(14, 7), SKY_LIGHT_BRIGHTNESS);
            //
            put(new Vector2(15, 0), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(15, 1), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(15, 2), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(15, 3), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(15, 4), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(15, 5), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(15, 6), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(15, 7), SKY_LIGHT_BRIGHTNESS);
            put(new Vector2(15, 8), SKY_LIGHT_BRIGHTNESS);

            //blocks that give out light

            put(new Vector2(0, 8), TileType.LAVA.getLuminosity());
            put(new Vector2(0, 9), TileType.LAVA.getLuminosity());
            put(new Vector2(1, 9), TileType.LAVA.getLuminosity());

        }};


        HashMap<Vector2, LightLevel> actual = renderer.getLightSources();

        System.out.println("actual = " + actual);

        MapDifference<Vector2, LightLevel> diff = Maps.difference(expected, actual);
        if (!diff.areEqual()) {
            Assertions.fail("Only in actual map: " + diff.entriesOnlyOnRight() + "\nonly in expected map: " +
                            diff.entriesOnlyOnLeft());
        }
    }
}