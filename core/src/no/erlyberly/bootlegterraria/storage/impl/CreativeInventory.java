package no.erlyberly.bootlegterraria.storage.impl;

import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.storage.AbstractInventory;
import no.erlyberly.bootlegterraria.storage.IContainer;
import no.erlyberly.bootlegterraria.storage.IInventory;
import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;

/**
 * A creative storage where most of the methods from {@link AbstractInventory} is not supported. Only {@link
 * #holding()} is implemented.
 */
public class CreativeInventory implements IInventory {

    private final Entity holder;
    private TileStack currHolding;
    private int curr;

    public CreativeInventory(final Entity holder) {
        this.holder = holder;
        select(0);
    }

    @Override
    public void next() {
        select(curr + 1);
    }

    @Override
    public void prev() {
        select(curr - 1);
    }

    @Override
    public TileStack holding() {
        return currHolding;
    }

    @Override
    public int getSize() {
        return TileType.TILE_TYPES;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public int getSel() {
        return curr;
    }

    @Override
    public Entity getHolder() {
        return holder;
    }

    @Override
    public void open() {
        holder.getGameMap().getUIMessage().sendMessage("Cannot open a creative inventory", 1500);
    }

    @Override
    public void close() {}

    @Override
    public IContainer getContainer() {
        return null;
    }

    @Override
    public void select(int index) {
        if (index < 0) {
            index = TileType.TILE_TYPES + (index % TileType.TILE_TYPES);
        }
        curr = index % TileType.TILE_TYPES;
        final TileType tt = TileType.getTileTypeById(curr + 1);
        currHolding = new TileStack(tt, 1);
    }


}
