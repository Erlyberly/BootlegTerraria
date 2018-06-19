package no.erlyberly.bootlegterraria.inventory;

import com.google.common.base.Preconditions;

/**
 * @author kheba
 */
public abstract class Inventory implements TileContainer {

    private int sel;

    /**
     * @return The item currently active
     */
    public abstract TileStack holding();

    /**
     * Select the next item
     */
    public void next() {
        this.sel = (this.sel + 1) % getSize();
    }

    /**
     * Select the previous item
     */
    public void previous() {
        this.sel = (this.sel - 1) % getSize();
    }

    /**
     * Select the {@code index}th item
     */
    public void sel(final int index) {
        this.sel = Preconditions.checkPositionIndex(index, getSize() - 1);
    }

    int getSel() {
        return this.sel;
    }
}
