package no.erlyberly.bootlegterraria.inventory;

import no.erlyberly.bootlegterraria.world.TileType;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author kheba
 */
class InventoryTest {

    private class TestInventory extends Inventory {

        private final int size;

        TestInventory(final int size) {

            this.size = size;
        }

        @Override
        public TileStack holding() {
            return null;
        }

        @Override
        public int getSize() {
            return this.size;
        }

        @Override
        public int firstEmpty() {
            return 0;
        }

        @Override
        public int first(final TileStack tileStack) {
            return 0;
        }

        @Override
        public List<TileStack> add(final List<TileStack> tileStack) {
            return null;
        }

        @Override
        public void removeAll(final TileType tileType) {

        }

        @Override
        public void remove(final TileStack... tileStack) {

        }

        @Override
        public void clear() {

        }

        @Override
        public boolean contains(final TileStack tileStack) {
            return false;
        }

        @Override
        public boolean containsAny(final TileType tileType) {
            return false;
        }

        @Override
        public TileStack[] getUnsafe(final int index, final boolean validate) {
            return new TileStack[0];
        }

        @Override
        public void put(final int index, final TileStack tileStack) {

        }

        @Override
        public Iterator<TileStack> iterator() {
            return null;
        }
    }

    @Test
    void next() {
        final TestInventory inv = new TestInventory(10);

        for (int i = 0; i < inv.getSize(); i++) {
            assertEquals(i, inv.getSel());
            inv.next();
        }
        //make sure the inventory is wrapped around correctly
        assertEquals(0, inv.getSel());
    }

    @Test
    void previous() {
        final TestInventory inv = new TestInventory(10);

        assertEquals(0, inv.getSel());
        inv.previous();
        assertEquals(inv.getSize() - 1, inv.getSel());

        for (int i = 0; i < inv.getSize(); i++) {
            assertEquals(i, inv.getSel());
            inv.previous();
        }
    }

    @Test
    void sel() {
        final TestInventory inv = new TestInventory(10);
        assertThrows(IndexOutOfBoundsException.class, () -> inv.sel(-1));
        inv.sel(1);
        assertThrows(IndexOutOfBoundsException.class, () -> inv.sel(inv.getSize()));
    }
}