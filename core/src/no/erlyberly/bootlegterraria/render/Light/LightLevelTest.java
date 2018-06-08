package no.erlyberly.bootlegterraria.render.light;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LightLevelTest {

    @Test
    void getLightLevel() {
        for (final LightLevel level : LightLevel.values()) {
            Assertions.assertEquals(level, LightLevel.valueOf(level.lvl));
        }
        Assertions.assertEquals(LightLevel.LVL_0, LightLevel.valueOf(-1));
        Assertions.assertNotEquals(LightLevel.LVL_7, LightLevel.valueOf(LightLevel.LIGHT_LEVELS));
        Assertions.assertEquals(LightLevel.LVL_8, LightLevel.valueOf(LightLevel.LIGHT_LEVELS));
    }
}