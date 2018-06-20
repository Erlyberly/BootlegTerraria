package no.erlyberly.bootlegterraria.util;

import no.erlyberly.bootlegterraria.inventory.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static no.erlyberly.bootlegterraria.util.SortOrder.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author kheba
 */
class SortOrderTest {

    @Test
    void tileTypeComparatorTest() {
        final TileStack[] unordered = {//
            new TileStack(TileType.STONE),//
            new TileStack(TileType.DIRT),//
            new TileStack(TileType.CLOUD),//
        };

        final TileStack[] ordered = {//
            new TileStack(TileType.CLOUD),//
            new TileStack(TileType.DIRT),//
            new TileStack(TileType.STONE),//
        };

        Arrays.sort(unordered, TT_NAME);

        assertArrayEquals(ordered, unordered);
    }

    @Test
    void amountComparatorTest() {
        final TileStack[] unordered = {//
            new TileStack(TileType.CLOUD, -2),//
            new TileStack(TileType.CLOUD, 91),//
            new TileStack(TileType.CLOUD, 4),//
        };

        final TileStack[] ordered = {//
            new TileStack(TileType.CLOUD, -2),//
            new TileStack(TileType.CLOUD, 4),//
            new TileStack(TileType.CLOUD, 91),//
        };

        Arrays.sort(unordered, AMOUNT_ASC);

        assertArrayEquals(ordered, unordered);
    }

    @Test
    void sortTile() {
        final TileStack[] unordered = {//
            new TileStack(TileType.DIRT, 8),//
            new TileStack(TileType.LEAVES, 84),//
            new TileStack(TileType.GRASS, 2),//
            new TileStack(TileType.CLOUD, 420),//
            new TileStack(TileType.DIRT, 150),//
            new TileStack(TileType.DIRT, 150),//
            new TileStack(TileType.STONE, 98),//
            new TileStack(TileType.STONE, 99),//
            new TileStack(TileType.DIRT, 68),//
            new TileStack(TileType.LOG, 28),//
        };

        final TileStack[] ordered1 = {//
            new TileStack(TileType.CLOUD, 420),//
            new TileStack(TileType.DIRT, 150),//
            new TileStack(TileType.DIRT, 150),//
            new TileStack(TileType.DIRT, 68),//
            new TileStack(TileType.DIRT, 8),//
            new TileStack(TileType.GRASS, 2),//
            new TileStack(TileType.LEAVES, 84),//
            new TileStack(TileType.LOG, 28),//
            new TileStack(TileType.STONE, 99),//
            new TileStack(TileType.STONE, 98),//
        };

        //noinspection unchecked
        SortOrder.sort(unordered, false, TT_NAME, AMOUNT_DESC);

        assertArrayEquals(ordered1, unordered);

        final TileStack[] ordered2 = {//
            new TileStack(TileType.CLOUD, 420),//
            new TileStack(TileType.DIRT, 8),//
            new TileStack(TileType.DIRT, 68),//
            new TileStack(TileType.DIRT, 150),//
            new TileStack(TileType.DIRT, 150),//
            new TileStack(TileType.GRASS, 2),//
            new TileStack(TileType.LEAVES, 84),//
            new TileStack(TileType.LOG, 28),//
            new TileStack(TileType.STONE, 98),//
            new TileStack(TileType.STONE, 99),//
        };

        //noinspection unchecked
        SortOrder.sort(unordered, false, TT_NAME, AMOUNT_ASC);

        assertArrayEquals(ordered2, unordered);

        //reverse ordered2 and check that the reversed flag works in SortOrder
        final List<TileStack> ordered3 = Arrays.asList(ordered2);
        Collections.reverse(ordered3);

        //noinspection unchecked
        SortOrder.sort(unordered, true, TT_NAME, AMOUNT_ASC);

        assertEquals(ordered3, Arrays.asList(unordered));
    }
}