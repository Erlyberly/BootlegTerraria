package no.erlyberly.bootlegterraria.storage;

import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import java.util.*;

import static no.erlyberly.bootlegterraria.storage.SortOrder.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kheba
 */
class SortOrderTest {

    @Test
    void tileTypeDESCComparatorTest() {
        final TileStack[] unordered = {//
            new TileStack(TileType.STONE),//
            new TileStack(TileType.DIRT),//
            null, //
            new TileStack(TileType.CLOUD),//
        };

        final TileStack[] ordered = {//
            new TileStack(TileType.CLOUD),//
            new TileStack(TileType.DIRT),//
            new TileStack(TileType.STONE),//
            null,//
        };

        Arrays.sort(unordered, TT_NAME_DESC);

//        System.out.println("unordered = " + Arrays.toString(unordered));

        assertArrayEquals(ordered, unordered);
    }

    @Test
    void tileTypeASCComparatorTest() {
        final TileStack[] unordered = {//
            new TileStack(TileType.STONE),//
            null, //
            new TileStack(TileType.DIRT),//
            new TileStack(TileType.CLOUD),//
        };

        final TileStack[] ordered = {//
            new TileStack(TileType.STONE),//
            new TileStack(TileType.DIRT),//
            new TileStack(TileType.CLOUD),//
            null,//
        };

        Arrays.sort(unordered, TT_NAME_ASC);

//        System.out.println("unordered = " + Arrays.toString(unordered));

        assertArrayEquals(ordered, unordered);
    }

    @Test
    void amountDESCComparatorTest() {
        final TileStack[] unordered = {//
            new TileStack(TileType.STONE, 1),//
            null, //
            new TileStack(TileType.STONE, 5),//
            new TileStack(TileType.STONE, 2),//
        };

        final TileStack[] ordered = {//
            new TileStack(TileType.STONE, 5),//
            new TileStack(TileType.STONE, 2),//
            new TileStack(TileType.STONE, 1),//
            null,//
        };

        Arrays.sort(unordered, AMOUNT_DESC);

//        System.out.println("unordered = " + Arrays.toString(unordered));

        assertArrayEquals(ordered, unordered);
    }

    @Test
    void amountASCComparatorTest() {
        final TileStack[] unordered = {//
            new TileStack(TileType.STONE, 1),//
            null, //
            new TileStack(TileType.STONE, 5),//
            new TileStack(TileType.STONE, 2),//
        };

        final TileStack[] ordered = {//
            new TileStack(TileType.STONE, 1),//
            new TileStack(TileType.STONE, 2),//
            new TileStack(TileType.STONE, 5),//
            null,//
        };

        Arrays.sort(unordered, AMOUNT_ASC);

//        System.out.println("unordered = " + Arrays.toString(unordered));

        assertArrayEquals(ordered, unordered);
    }


    @Test
    void tileTypeComparatorNullTest() {
        final TileStack[] unordered = {//
            null,//
            null,//
            new TileStack(TileType.CLOUD),//
        };

        final TileStack[] ordered = {//
            new TileStack(TileType.CLOUD),//
            null,//
            null,//
        };

        final TileStack[] unordered2 = Arrays.copyOf(unordered, unordered.length);

        //noinspection unchecked
        SortOrder.sort(unordered, false, TT_NAME_DESC);
        assertArrayEquals(ordered, unordered);

        Arrays.sort(unordered2, TT_NAME_DESC);
        assertArrayEquals(ordered, unordered2);

        assertArrayEquals(unordered, unordered2);
    }

    @Test
    void tileTypeComparatorOnlyNullTest() {
        final TileStack[] unorderedOrg = {//
            null,//
            null,//
            null,//
        };

        final TileStack[] ordered = {//
            null,//
            null,//
            null,//
        };

        final HashSet<Comparator<TileStack>> compSet = new HashSet<Comparator<TileStack>>() {{
            add(TT_NAME_ASC);
            add(TT_NAME_DESC);
            add(AMOUNT_ASC);
            add(AMOUNT_DESC);
        }};


        for (final Set<Comparator<TileStack>> s : Util.powerSet(compSet)) {
            final TileStack[] unordered = unorderedOrg.clone();

            if (s.isEmpty()) {
                //noinspection unchecked
                assertThrows(IllegalArgumentException.class,
                             () -> SortOrder.sort(unordered, false, s.toArray(new Comparator[0])));
                continue;
            }

//            System.out.println("Now sorting with " + s);

            //noinspection unchecked
            SortOrder.sort(unordered, false, s.toArray(new Comparator[0]));
            assertArrayEquals(ordered, unordered);
        }


    }

    @Test
    void amountComparatorTest() {
        final TileStack[] unordered = {//
            new TileStack(TileType.CLOUD, 0),//
            new TileStack(TileType.CLOUD, 91),//
            new TileStack(TileType.CLOUD, 4),//
        };

        final TileStack[] ordered = {//
            new TileStack(TileType.CLOUD, 0),//
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
            null,//
            null,//
            new TileStack(TileType.GRASS, 2),//
            new TileStack(TileType.CLOUD, 420),//
            new TileStack(TileType.DIRT, 150),//
            new TileStack(TileType.DIRT, 150),//
            new TileStack(TileType.STONE, 98),//
            null,//
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
            null,//
            null,//
            null,//
        };

        //noinspection unchecked
        SortOrder.sort(unordered, false, TT_NAME_DESC, AMOUNT_DESC);

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
            null,//
            null,//
            null,//
        };

        //noinspection unchecked
        SortOrder.sort(unordered, false, TT_NAME_DESC, AMOUNT_ASC);

        assertArrayEquals(ordered2, unordered);

        //reverse ordered2 and check that the reversed flag works in SortOrder
        final List<TileStack> ordered3 = Arrays.asList(ordered2);
        Collections.reverse(ordered3);

        //noinspection unchecked
        SortOrder.sort(unordered, true, TT_NAME_DESC, AMOUNT_ASC);

        assertEquals(ordered3, Arrays.asList(unordered));
    }
}