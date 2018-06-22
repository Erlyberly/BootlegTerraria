package no.erlyberly.bootlegterraria.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author kheba
 */
class AbstractInventoryTest {

    private class TestAbstractInventory extends AbstractInventory {

        private final int size;

        TestAbstractInventory(final int size) {
            super(null);
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
    }

    @Test
    void next() {
        final TestAbstractInventory inv = new TestAbstractInventory(10);

        for (int i = 0; i < inv.getSize(); i++) {
            assertEquals(i, inv.getSel());
            inv.next();
        }
        //make sure the inventory is wrapped around correctly
        assertEquals(0, inv.getSel());
    }

    @Test
    void previous() {
        final TestAbstractInventory inv = new TestAbstractInventory(10);

        assertEquals(0, inv.getSel());
        inv.prev();
        assertEquals(inv.getSize() - 1, inv.getSel());

        for (int i = inv.getSize() - 1; i >= 0; i--) {
            assertEquals(i, inv.getSel(), "i: " + i);
            inv.prev();
        }
    }

    @Test
    void sel() {
        final TestAbstractInventory inv = new TestAbstractInventory(10);
        assertThrows(IndexOutOfBoundsException.class, () -> inv.select(-1));
        for (int i = 0; i < inv.getSize(); i++) {
            inv.select(i);
            assertEquals(i, inv.getSel(), "i=" + i);
        }
        assertThrows(IndexOutOfBoundsException.class, () -> inv.select(inv.getSize()));
    }
}