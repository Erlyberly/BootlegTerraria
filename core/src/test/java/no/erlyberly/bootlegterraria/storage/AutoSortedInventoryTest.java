package no.erlyberly.bootlegterraria.storage;

import no.erlyberly.bootlegterraria.storage.impl.AutoSortedInventory;
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