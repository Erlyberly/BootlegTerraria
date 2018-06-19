package no.erlyberly.bootlegterraria.inventory;

import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author kheba
 */
class AutoSortedInventoryTest {

    @Test
    void holding() {
        final AutoSortedInventory asi = new AutoSortedInventory(10);
        assertNull(asi.holding()); //hold nothing when first initialized

    }

    @Test
    void getSize() {
        final AutoSortedInventory asi = new AutoSortedInventory(10);
        assertEquals(10, asi.getSize());
    }

    @Test
    void firstEmpty() {
        final AutoSortedInventory asi = new AutoSortedInventory(10);
        assertEquals(0, asi.firstEmpty());
        asi.add(new TileStack(TileType.DIRT, 1));
        assertEquals(1, asi.firstEmpty());
    }

    @Test
    void first() {
        final AutoSortedInventory asi = new AutoSortedInventory(10);
//        assertEquals(null,asi.fir);
    }

    @Test
    void add() {
        final AutoSortedInventory asi = new AutoSortedInventory(10);
        asi.add(new TileStack(TileType.DIRT, 25));
    }

    @Test
    void removeAll() {
    }

    @Test
    void remove() {
    }

    @Test
    void remove1() {
    }

    @Test
    void clear() {
    }

    @Test
    void contains() {
    }

    @Test
    void containsAny() {
    }

    @Test
    void get() {
    }

    @Test
    void iterator() {
    }
}