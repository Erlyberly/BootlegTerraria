package no.erlyberly.bootlegterraria.inventory;

import org.junit.jupiter.api.Test;

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
}