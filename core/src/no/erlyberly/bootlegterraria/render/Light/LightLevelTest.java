package no.erlyberly.bootlegterraria.render.Light;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LightLevelTest {

    @Test
    void getLightLevel() {
        for (LightLevel level : LightLevel.values()) {
            Assertions.assertEquals(level, LightLevel.valueOf(level.lvl));
        }
        Assertions.assertEquals(LightLevel.LVL_0, LightLevel.valueOf(-1));
        Assertions.assertEquals(LightLevel.LVL_7, LightLevel.valueOf(LightLevel.LIGHT_LEVELS));
    }
}