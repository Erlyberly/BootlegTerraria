package no.erlyberly.bootlegterraria.storage.impl;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.storage.SortOrder;
import no.erlyberly.bootlegterraria.storage.TileStack;

/**
 * A container that will auto sort the storage when updated
 *
 * @author kheba
 */
public class AutoSortedContainer extends Container {

    //do not allow change of valid stacks as the implementation would be hard to do correctly
    private final boolean disallowInvalid;

    //You can modify the sortOrder directly
    private final SortOrder sortOrder;

    /**
     * A normal container that disallows invalid {@link TileStack}s and sorts first by name then by tile amount in an
     * descending order, with the name 'Container'
     *
     * @param size
     *     The size of the container
     */
    public AutoSortedContainer(final int size) {
        this("Auto Sorted Container", size);
    }

    /**
     * A normal container that disallows invalid {@link TileStack}s and sorts first by name then by tile amount in an
     * descending order
     *
     * @param size
     *     The size of the container
     */
    public AutoSortedContainer(final String name, final int size) {
        this(name, size, true);
    }

    /**
     * A container that sorts first by name then by tile amount in an descending order
     *
     * @param size
     *     The size of the container
     * @param disallowInvalid
     *     if this container does not allow invalid {@link TileStack}s
     */
    public AutoSortedContainer(final String name, final int size, final boolean disallowInvalid) {
        this(name, size, disallowInvalid, new SortOrder(false, SortOrder.TT_NAME_DESC, SortOrder.AMOUNT_DESC));
    }

    /**
     * @param size
     *     The size of the container
     * @param disallowInvalid
     *     if this container does not allow invalid {@link TileStack}s
     * @param sortOrder
     *     The way to sort the validate each time it is modified
     */
    public AutoSortedContainer(final String name, final int size, final boolean disallowInvalid,
                               final SortOrder sortOrder) {
        super(name, size, disallowInvalid);
        Preconditions.checkArgument(size > 0, "Inventory size must be greater than zero");
        Preconditions.checkNotNull(sortOrder, "The sort order cannot be null");

        this.disallowInvalid = disallowInvalid;
        this.sortOrder = sortOrder;
    }

    @Override
    public void updateContainer() {
        super.updateContainer();
        sortOrder.sort(cont);
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    @Override
    public void put(final int index, final TileStack tileStack) {
        Preconditions.checkPositionIndex(index, getSize() - 1);
        if (isValidOnly() && tileStack != null && !tileStack.isValid()) {
            throw new IllegalArgumentException("This container does not allow invalid stacks");
        }
        cont[index] = null;
        add(tileStack);
    }
}
