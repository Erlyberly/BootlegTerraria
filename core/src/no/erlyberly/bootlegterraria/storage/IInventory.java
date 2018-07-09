package no.erlyberly.bootlegterraria.storage;

import no.erlyberly.bootlegterraria.entities.Entity;

/**
 * @author kheba
 */
public interface IInventory {

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
     * @return The size of the storage
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
     * @return The entity that holds this container. Only this entity should have access to the container, if it is
     * {@code null} everyone should be able to modify and see the content of this container
     */
    Entity getHolder();

    /**
     * @param entity
     *     The entity to check for ownership
     *
     * @return If the given entity can view/modify the content of the storage
     */
    default boolean isHolder(final Entity entity) {
        return getHolder() == null || getHolder().equals(entity);
    }

    /**
     * Open a GUI of this inventory
     */
    void open();

    /**
     * Close the GUI of this inventory
     */
    void close();

    /**
     * @return The container of this inventory
     */
    IContainer getContainer();
}
