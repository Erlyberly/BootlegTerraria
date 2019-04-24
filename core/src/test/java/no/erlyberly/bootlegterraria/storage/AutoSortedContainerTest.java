package no.erlyberly.bootlegterraria.storage;

import no.erlyberly.bootlegterraria.storage.impl.AutoSortedContainer;
import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author kheba
 */
class AutoSortedContainerTest {

    private void testGet(final TileStack expected, final int index, final AutoSortedContainer cont) {
        assertEquals(expected, cont.getContent()[index]);
        assertEquals(expected, cont.get(index));
    }

    @Test
    void constructor() {
        assertThrows(IllegalArgumentException.class, () -> new AutoSortedContainer(0));
        assertThrows(IllegalArgumentException.class, () -> new AutoSortedContainer(-1));
    }

    @Test
    void getSizeTest() {
        final Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            final int ran = Math.abs(r.nextInt(10000) + 1);
            assertEquals(ran, new AutoSortedContainer(ran).getSize());
        }
    }

    @Test
    void putTest() {
        final AutoSortedContainer cont = new AutoSortedContainer(10);

        final TileStack ts = new TileStack(TileType.DIRT, 1);
        cont.put(0, ts);

        testGet(ts, 0, cont);

        cont.put(2, ts);
        //make sure the container will be merged and sorted
        testGet(null, 1, cont);
        testGet(null, 2, cont);
    }

    @Test
    void firstEmptyTest() {
        for (final TileType tt : TileType.values()) {
            final AutoSortedContainer cont = new AutoSortedContainer(3);
            for (int i = 0; i < cont.getSize() - 1; i++) {
                cont.put(i, new TileStack(tt, tt.getMaxStackSize()));
                assertEquals(i + 1, cont.firstEmpty());
            }
            cont.put(cont.getSize() - 1, new TileStack(tt, tt.getMaxStackSize()));
            assertEquals(-1, cont.firstEmpty());
        }
    }

    @Test
    void firstTest() {
        final AutoSortedContainer cont = new AutoSortedContainer(10);
//       testGet(ts, 0, cont);
    }

    @Test
    void addTest2() {
        final AutoSortedContainer cont =
            new AutoSortedContainer("test", 40, true, new SortOrder(false, SortOrder.TT_NAME_DESC, SortOrder.AMOUNT_DESC));

        final TileStack tsCloud = new TileStack(TileType.CLOUD, 1);
        final TileStack tsTorch = new TileStack(TileType.TORCH, 2);

        cont.add(tsCloud, tsTorch);

//        System.out.println("cont.getContent() = " + Arrays.toString(cont.getContent()));

        testGet(tsCloud, 0, cont);
        testGet(tsTorch, 1, cont);
    }

    @Test
    void addTest() {
        final AutoSortedContainer cont = new AutoSortedContainer(1);

        final int amount = 25;

        final TileStack tsDirt = new TileStack(TileType.DIRT, amount);
        final List<TileStack> returned = cont.add(tsDirt.clone());

        assertTrue(returned.isEmpty());
        assertEquals(tsDirt, cont.getContent()[0]);
        assertEquals(tsDirt, cont.get(0));

        //ts' amount should be doubled
        final List<TileStack> returned2 = cont.add(tsDirt);

        assertTrue(returned2.isEmpty());
        assertEquals(amount * 2, tsDirt.getAmount());
        assertEquals(tsDirt, cont.getContent()[0]);
        assertEquals(tsDirt, cont.get(0));

        final TileStack tsCloud = new TileStack(TileType.CLOUD, 1);

        //returned3 should return the added element
        final List<TileStack> returned3 = cont.add(tsCloud);

        assertFalse(returned3.isEmpty());
        assertEquals(Collections.singletonList(tsCloud), returned3);
    }

    @Test
    void containsTest() {
        final AutoSortedContainer cont = new AutoSortedContainer("", 10, false);

        final TileStack ts = new TileStack(TileType.DIRT, 25);
        cont.put(0, ts);
        assertFalse(cont.contains(null));
        assertTrue(cont.contains(ts));
        assertTrue(cont.contains(new TileStack(TileType.DIRT, 25)));
    }

    @Test
    void iteratorTest() {
        final AutoSortedContainer cont = new AutoSortedContainer("", 10, false);
        final Iterator<ContainerSlot> itr = cont.iterator();
        for (int i = 0; i <= cont.getSize(); i++) {
            //make sure that has next is working
            if (i == cont.getSize()) {
                assertFalse(itr.hasNext());
                break;
            }
            else {
                assertTrue(itr.hasNext());
            }

            final ContainerSlot slot = itr.next();
            assertEquals(i, slot.getIndex());
        }
    }

}
