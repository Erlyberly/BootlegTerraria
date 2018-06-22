package no.erlyberly.bootlegterraria.inventory;

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
}
