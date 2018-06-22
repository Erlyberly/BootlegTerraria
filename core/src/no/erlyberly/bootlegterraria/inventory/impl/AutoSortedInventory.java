package no.erlyberly.bootlegterraria.inventory.impl;

import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.inventory.AbstractInventory;
import no.erlyberly.bootlegterraria.inventory.TileStack;

/**
 * An inventory intended to be used by players
 *
 * @author kheba
 */
public class AutoSortedInventory extends AbstractInventory {

    private final AutoSortedContainer inv;

    public AutoSortedInventory(final int size, final Entity holder) {
        super(holder);
        this.inv = new AutoSortedContainer(size);
    }

    @Override
    public TileStack holding() {
        return this.inv.get(getSel());
    }

    @Override
    public int getSize() {
        return this.inv.getSize();
    }
}
