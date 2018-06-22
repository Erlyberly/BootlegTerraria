package no.erlyberly.bootlegterraria.inventory.impl;

import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.inventory.AbstractInventory;
import no.erlyberly.bootlegterraria.inventory.Inventory;
import no.erlyberly.bootlegterraria.inventory.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;

/**
 * A creative inventory where most of the methods from {@link AbstractInventory} is not supported. Only {@link
 * #holding()} is
 * implemented.
 */
public class CreativeInventory implements Inventory {

    private final Entity holder;
    private TileStack currHolding;
    private int curr;

    public CreativeInventory(final Entity holder) {
        this.holder = holder;
        select(0);
    }

    @Override
    public void next() {
        select(this.curr + 1);
    }

    @Override
    public void prev() {
        select(this.curr - 1);
    }

    @Override
    public TileStack holding() {
        return this.currHolding;
    }

    @Override
    public int getSize() {
        return TileType.TILE_TYPES;
    }

    @Override
    public int getSel() {
        return this.curr;
    }

    @Override
    public Entity getHolder() {
        return this.holder;
    }

    @Override
    public void select(int index) {
        System.out.println("index = [" + index + "]");
        if (index < 0) {
            index = TileType.TILE_TYPES + (index % TileType.TILE_TYPES);
        }
        this.curr = index % TileType.TILE_TYPES;
        final TileType tt = TileType.getTileTypeById(this.curr + 1);
        this.currHolding = new TileStack(tt, 1);
    }
}
