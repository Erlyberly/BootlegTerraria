package no.erlyberly.bootlegterraria.inventory;

import no.erlyberly.bootlegterraria.entities.Entity;

/**
 * @author kheba
 */
public interface Inventory {

    /**
     * @return The item currently active
     */
    TileStack holding();

    /**
     * Select the next item
     */
    void next();

    /**
     * Select the previous item
     */
    void prev();

    /**
     * @return The size of the inventory
     */
    int getSize();

    /**
     * Select the {@code index}-th item
     */
    void select(final int index);

    /**
     * @return The index of the selected item
     */
    int getSel();

    /**
     * @return Owner of this inventory
     */
    Entity getHolder();
}
