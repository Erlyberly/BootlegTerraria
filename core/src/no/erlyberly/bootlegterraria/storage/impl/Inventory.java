package no.erlyberly.bootlegterraria.storage.impl;

import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.storage.AbstractInventory;
import no.erlyberly.bootlegterraria.storage.IContainer;
import no.erlyberly.bootlegterraria.storage.TileStack;

/**
 * An storage intended to be used by living entities
 *
 * @author kheba
 */
public class Inventory extends AbstractInventory {

    private final IContainer container;

    public Inventory(final Entity holder, final int size) {
        this(holder, new AutoSortedContainer("Inventory", size));
    }

    public Inventory(final Entity holder, final IContainer container) {
        super(holder);
        this.container = container;
    }


    @Override
    public TileStack holding() {
        return container.get(getSel());
    }

    @Override
    public int getSize() {
        return container.getSize();
    }

    @Override
    public IContainer getContainer() {
        return container;
    }
}
