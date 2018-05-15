package no.erlyberly.bootlegterraria.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LightLevelTest {

    @Test
    void getLightLevel() {
        for (LightLevel level : LightLevel.values()) {
            Assertions.assertEquals(level, LightLevel.fromLevel(level.lvl));
        }
        Assertions.assertEquals(LightLevel.LVL_0, LightLevel.fromLevel(-1));
        Assertions.assertEquals(LightLevel.LVL_7, LightLevel.fromLevel(LightLevel.LIGHT_LEVELS));
    }
}