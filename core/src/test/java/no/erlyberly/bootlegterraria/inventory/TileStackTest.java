package no.erlyberly.bootlegterraria.inventory;

import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * @author kheba
 */
class TileStackTest {

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
            assertArrayEquals(ets4, ats4);
        }
    }
}