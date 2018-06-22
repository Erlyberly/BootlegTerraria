package no.erlyberly.bootlegterraria.inventory;

import no.erlyberly.bootlegterraria.inventory.impl.AutoSortedInventory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author kheba
 */
class AutoSortedInventoryTest {


    @Test
    void holding() {
        final AutoSortedInventory asi = new AutoSortedInventory(10, null);
        assertNull(asi.holding()); //hold nothing when first initialized

    }
}