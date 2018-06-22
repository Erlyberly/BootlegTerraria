package no.erlyberly.bootlegterraria.inventory;

/**
 * An inventory intended to be used by players
 *
 * @author kheba
 */
public class AutoSortedInventory extends AbstractInventory {

    private final AutoSortedContainer inv;

    public AutoSortedInventory(final int size) {
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
