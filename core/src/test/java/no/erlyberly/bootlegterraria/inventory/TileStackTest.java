package no.erlyberly.bootlegterraria.inventory;

import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kheba
 */
class TileStackTest {

    @Test
    void negAmount() {
        assertThrows(IllegalArgumentException.class, () -> new TileStack(TileType.CLOUD, -1));
        assertThrows(IllegalArgumentException.class, () -> new TileStack(TileType.CLOUD, 1).setAmount(-1));
        assertThrows(NullPointerException.class, () -> new TileStack(null, 1));
    }

    @Test
    void validity() {
        for (final TileType tt : TileType.values()) {
            //zero should always be allowed
            assertTrue(new TileStack(tt, 0).isValid());

            final TileStack ts1 = new TileStack(tt, 0);

            ts1.setAmount(tt.getMaxStackSize());
            assertTrue(ts1.isValid());

            ts1.add(1);
            assertFalse(ts1.isValid());
        }
    }

    @Test
    void stack() {
        for (final TileType tt : TileType.values()) {

            //only one stack is returned
            final TileStack[] ats = TileStack.stack(tt, 25);
            final TileStack[] ets = { //
                new TileStack(tt, 25) //
            };
            assertArrayEquals(ets, ats);

            //exactly two stacks returned
            final TileStack[] ats2 = TileStack.stack(tt, tt.getMaxStackSize() * 2);
            final TileStack[] ets2 = { //
                new TileStack(tt, tt.getMaxStackSize()), //
                new TileStack(tt, tt.getMaxStackSize()) //
            };
            assertArrayEquals(ets2, ats2);

            //two stacks returned, one full, another with just one
            final TileStack[] ats3 = TileStack.stack(tt, tt.getMaxStackSize() + 1);
            final TileStack[] ets3 = { //
                new TileStack(tt, 1), //
                new TileStack(tt, tt.getMaxStackSize()) //
            };
            assertArrayEquals(ets3, ats3);

            //exactly one stacks returned
            final TileStack[] ats4 = TileStack.stack(tt, tt.getMaxStackSize());
            final TileStack[] ets4 = { //
                new TileStack(tt, tt.getMaxStackSize()) //
            };
            assertArrayEquals(ets4, ats4);

            //exactly one stacks returned
            final TileStack[] ats5 = TileStack.stack(tt, 0);
            final TileStack[] ets5 = { //
            };
            assertArrayEquals(ets5, ats5);
        }
    }
}