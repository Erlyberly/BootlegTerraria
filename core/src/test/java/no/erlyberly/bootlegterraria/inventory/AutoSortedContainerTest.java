package no.erlyberly.bootlegterraria.inventory;

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

        assertEquals(ts, cont.getContent()[0]);
        assertEquals(ts, cont.get(0));

        cont.put(2, ts);
        //make sure the container will be merged and sorted
        assertNull(cont.getContent()[1]);
        assertNull(cont.get(1));
        assertNull(cont.getContent()[2]);
        assertNull(cont.get(2));
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
//        assertEquals(null,cont.fir);
    }

    @Test
    void addTest() {
        final AutoSortedContainer cont = new AutoSortedContainer(1);

        final int amount = 25;

        final TileStack tsDirt = new TileStack(TileType.DIRT, amount);
        final List<TileStack> returned = cont.add(tsDirt);

        assertTrue(returned.isEmpty());
        assertEquals(tsDirt, cont.getContent()[0]);
        assertEquals(tsDirt, cont.get(0));
        assertEquals(amount, cont.getContent()[0].getAmount());
        assertEquals(amount, cont.get(0).getAmount());

        //ts' amount should be doubled
        final List<TileStack> returned2 = cont.add(tsDirt);

        assertTrue(returned2.isEmpty());
        assertEquals(tsDirt, cont.getContent()[0]);
        assertEquals(tsDirt, cont.get(0));
        assertEquals(amount * 2, tsDirt.getAmount());

        final TileStack tsCloud = new TileStack(TileType.CLOUD, 1);

        //ts' amount should be doubled
        final List<TileStack> returned3 = cont.add(tsCloud);

        assertFalse(returned3.isEmpty());
        assertEquals(Collections.singletonList(tsCloud), returned3);
    }

    @Test
    void containsTest() {
        final AutoSortedContainer cont = new AutoSortedContainer(10, false);

        final TileStack ts = new TileStack(TileType.DIRT, 25);
        cont.put(0, ts);
        assertFalse(cont.contains(null));
        assertTrue(cont.contains(ts));
        assertTrue(cont.contains(new TileStack(TileType.DIRT, 25)));
    }

    @Test
    void iteratorTest() {
        final AutoSortedContainer cont = new AutoSortedContainer(10, false);
        final Iterator<Slot> itr = cont.iterator();
        for (int i = 0; i <= cont.getSize(); i++) {
            //make sure that has next is working
            if (i == cont.getSize()) {
                assertFalse(itr.hasNext());
                break;
            }
            else {
                assertTrue(itr.hasNext());
            }

            final Slot slot = itr.next();
            assertEquals(i, slot.getIndex());
        }
    }

}