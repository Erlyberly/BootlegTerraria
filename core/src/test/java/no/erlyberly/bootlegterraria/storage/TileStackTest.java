package no.erlyberly.bootlegterraria.storage;

import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

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

            ts1.give(1);
            assertFalse(ts1.isValid());
        }
    }

    @Test
    void stack() {
        for (final TileType tt : TileType.values()) {

            //only one validate is returned
            final TileStack[] ats = TileStack.validate(tt, 25);
            final TileStack[] ets = { //
                new TileStack(tt, 25) //
            };
            assertArrayEquals(ets, ats);

            //exactly two stacks returned
            final TileStack[] ats2 = TileStack.validate(tt, tt.getMaxStackSize() * 2);
            final TileStack[] ets2 = { //
                new TileStack(tt, tt.getMaxStackSize()), //
                new TileStack(tt, tt.getMaxStackSize()) //
            };
            assertArrayEquals(ets2, ats2);

            //two stacks returned, one full, another with just one
            final TileStack[] ats3 = TileStack.validate(tt, tt.getMaxStackSize() + 1);
            final TileStack[] ets3 = { //
                new TileStack(tt, 1), //
                new TileStack(tt, tt.getMaxStackSize()) //
            };
            Arrays.sort(ats3);
            Arrays.sort(ets3);
            assertArrayEquals(ets3, ats3);

            //exactly one stacks returned
            final TileStack[] ats4 = TileStack.validate(tt, tt.getMaxStackSize());
            final TileStack[] ets4 = { //
                new TileStack(tt, tt.getMaxStackSize()) //
            };
            assertArrayEquals(ets4, ats4);

            //exactly one stacks returned
            final TileStack[] ats5 = TileStack.validate(tt, 0);
            final TileStack[] ets5 = { //
            };
            assertArrayEquals(ets5, ats5);
        }
    }

    @Test
    void validate() {
        TileType tt = TileType.CLOUD;
        assertArrayEquals(TileStack.validate(new TileStack(tt, tt.getMaxStackSize() + 1)),
                          new TileStack[] {new TileStack(tt, tt.getMaxStackSize()), new TileStack(tt, 1)});

        assertArrayEquals(TileStack.validate(new TileStack(tt, tt.getMaxStackSize())),
                          new TileStack[] {new TileStack(tt, tt.getMaxStackSize())});

        assertArrayEquals(TileStack.validate(new TileStack(tt, tt.getMaxStackSize() * 2)),
                          new TileStack[] {new TileStack(tt, tt.getMaxStackSize()), new TileStack(tt, tt.getMaxStackSize())});

        assertArrayEquals(TileStack.validate(new TileStack(tt, tt.getMaxStackSize() * 3)),
                          new TileStack[] {new TileStack(tt, tt.getMaxStackSize()), new TileStack(tt, tt.getMaxStackSize()),
                              new TileStack(tt, tt.getMaxStackSize())});
    }
}
