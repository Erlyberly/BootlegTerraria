package no.erlyberly.bootlegterraria.storage;

import no.erlyberly.bootlegterraria.storage.impl.Inventory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author kheba
 */
class InventoryTest {


    @Test
    void holding() {
        final Inventory asi = new Inventory(null, 10);
        assertNull(asi.holding()); //hold nothing when first initialized

    }
}