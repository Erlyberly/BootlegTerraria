package no.erlyberly.bootlegterraria.storage.impl;

import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author kheba
 */
class CreativeInventoryTest {

    @Test
    void next() {
        final CreativeInventory ci = new CreativeInventory(null);

        for (int i = 0; i < ci.getSize() - 1; i++) {
            assertEquals(new TileStack(TileType.values()[i], 1), ci.holding(), "i=" + i);
            ci.next();
        }
        assertEquals(new TileStack(TileType.values()[ci.getSize() - 1], 1), ci.holding());
        //make sure it wraps around
        ci.next();
        assertEquals(new TileStack(TileType.values()[0], 1), ci.holding());
    }

    @Test
    void prev() {
        final CreativeInventory ci = new CreativeInventory(null);
        ci.prev();
        assertEquals(new TileStack(TileType.values()[ci.getSize() - 1], 1), ci.holding());
    }

    @Test
    void select() {
        final CreativeInventory ci = new CreativeInventory(null);
        for (int i = 0; i < ci.getSize() * 2; i++) {
            ci.select(i);
            assertEquals(new TileStack(TileType.values()[i % ci.getSize()], 1), ci.holding(), "i = " + i);
        }

        ci.select(ci.getSize());
        assertEquals(new TileStack(TileType.values()[0], 1), ci.holding());

        //allow for selection of negative indexes
        ci.select(-1);
        assertEquals(new TileStack(TileType.values()[ci.getSize() - 1], 1), ci.holding());
        ci.select(-2);
        assertEquals(new TileStack(TileType.values()[ci.getSize() - 2], 1), ci.holding());

        ci.select(-ci.getSize());
        assertEquals(new TileStack(TileType.values()[0], 1), ci.holding());
    }


    @Test
    void holding() {
        final CreativeInventory ci = new CreativeInventory(null);
        assertNotNull(ci.holding());

        assertEquals(new TileStack(TileType.values()[0], 1), ci.holding());
    }

    @Test
    void getSel() {
        final CreativeInventory ci = new CreativeInventory(null);
        assertEquals(0, ci.getSel());
    }
}