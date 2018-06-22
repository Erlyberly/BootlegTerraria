package no.erlyberly.bootlegterraria.inventory;

import com.google.common.base.Preconditions;

/**
 * @author kheba
 */
public abstract class AbstractInventory implements Inventory {

    private int sel;

    @Override
    public void next() {
        this.sel = (this.sel + 1) % getSize();
    }

    @Override
    public void prev() {
        if (this.sel == 0) {
            this.sel = getSize() - 1;
            return;
        }
        this.sel = (this.sel - 1) % getSize();
    }

    @Override
    public void select(final int index) {
        this.sel = Preconditions.checkPositionIndex(index, getSize() - 1);
    }

    @Override
    public int getSel() {
        return this.sel;
    }
}
