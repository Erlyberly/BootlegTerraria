package no.erlyberly.bootlegterraria.storage.impl;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.storage.ContainerSlot;
import no.erlyberly.bootlegterraria.storage.IContainer;
import no.erlyberly.bootlegterraria.storage.SortOrder;
import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container that will auto sort the storage when updated
 *
 * @author kheba
 */
public class AutoSortedContainer implements IContainer {

    //do not allow change of size as the implementation would be hard to do correctly
    private final int size;
    //do not allow change of valid stacks as the implementation would be hard to do correctly
    private final boolean disallowInvalid;

    //You can modify the sortOrder directly
    private final SortOrder sortOrder;

    private final TileStack[] inv;

    private String name;

    /**
     * A normal container that disallows invalid {@link TileStack}s and sorts first by name then by tile amount in an
     * descending order, with the name 'Container'
     *
     * @param size
     *     The size of the container
     */
    public AutoSortedContainer(final int size) {
        this("Container", size);
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
        this(name, size, disallowInvalid, new SortOrder(false, SortOrder.TT_NAME_ASC, SortOrder.AMOUNT_DESC));
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
        Preconditions.checkArgument(size > 0, "Inventory size must be greater than zero");
        Preconditions.checkNotNull(sortOrder, "The sort order cannot be null");

        this.disallowInvalid = disallowInvalid;
        this.size = size;
        this.sortOrder = sortOrder;
        this.name = name;

        this.inv = new TileStack[size];
    }


    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int firstEmpty() {
        for (int i = 0; i < this.size; i++) {
            if (this.inv[i] == null) { return i; }
        }
        return -1;
    }

    @Override
    public int first(final TileStack tileStack) {
        if (tileStack == null) {
            return firstEmpty();
        }
        // if invalid stacks is not allows this cannot have a invalid validate
        if (this.disallowInvalid && !tileStack.isValid()) {
            return -1;
        }
        for (int i = 0; i < this.size; i++) {
            final TileStack loopTs = this.inv[i];
            if (loopTs != null && loopTs.getTileType() == tileStack.getTileType() &&
                loopTs.getAmount() <= tileStack.getAmount()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public List<TileStack> add(final List<TileStack> tileStacks) {
        Preconditions.checkNotNull(tileStacks, "Cannot add a null list");

        final List<TileStack> returnList = new ArrayList<>(tileStacks.size());

        for (int i = 0, size1 = tileStacks.size(); i < size1; i++) {
            final TileStack ts = tileStacks.get(i);
            if (ts == null || (this.disallowInvalid && !ts.isValid())) {
                throw new IllegalArgumentException("Element nr " + i + " is either null or invalid (" + ts + ")");
            }

            boolean added = false;

            for (int j = 0; j < this.size; j++) {
                if (this.inv[j] != null && this.inv[j].getTileType() == ts.getTileType()) {
                    if (this.inv[j].getAmount() + ts.getAmount() <= ts.getTileType().getMaxStackSize()) {
                        this.inv[j].give(ts.getAmount());
                        added = true;
                        break;
                    }
                }
            }
            //add item to the list at the first empty position
            if (!added) {
                final int firstEmpty = firstEmpty();
                if (firstEmpty >= 0) {
                    this.inv[firstEmpty] = ts;
                    added = true;
                }
            }
            if (!added) {
                returnList.add(ts);
            }
        }

        sort();
        return returnList;
    }

    @Override
    public void removeAll(final TileType tileType) {
        for (int i = 0; i < this.size; i++) {
            if (this.inv[i] != null && this.inv[i].getTileType() == tileType) {
                this.inv[i] = null;
            }
        }
        sort();
    }

    @Override
    public int remove(final TileType tileType, final int amount) {
        Preconditions.checkArgument(amount > 0, "The amount to remove must be positive, but was " + amount);
        int counter = amount;
        for (int i = 0, length = this.inv.length; i < length; i++) {
            if (this.inv[i] != null && tileType == this.inv[i].getTileType()) {
                final int newAmount = this.inv[i].getAmount() - counter;
                if (newAmount < 0) {
                    counter -= this.inv[i].getAmount();
                    this.inv[i] = null;
                }
                else {
                    if (newAmount != 0) { this.inv[i].setAmount(newAmount); }
                    else { this.inv[i] = null; }
                    sort();
                    return 0;
                }
            }
        }
        sort();
        return counter;
    }

    @Override
    public void remove(final TileStack tileStack) {
        Preconditions.checkNotNull(tileStack, "cannot remove a null element");
        if (this.disallowInvalid && !tileStack.isValid()) {
            return;
        }
        for (int i = 0, length = this.inv.length; i < length; i++) {
            if (tileStack.equals(this.inv[i])) {
                this.inv[i] = null;
            }
        }
        sort();
    }

    @Override
    public void remove(final int index) {
        Preconditions.checkPositionIndex(index, this.size - 1);
        this.inv[index] = null;
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.inv.length; i++) {
            this.inv[i] = null;
        }
    }

    @Override
    public boolean contains(final TileStack tileStack) {
        if (tileStack == null || (this.disallowInvalid && !tileStack.isValid())) {
            return false;
        }
        for (final ContainerSlot slot : this) {
            if (tileStack.equals(slot.getContent())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(final TileType tileType) {

        return false;
    }

    @Override
    public TileStack get(final int index) {
        Preconditions.checkPositionIndex(index, this.size - 1);
        return this.inv[index];
    }

    @Override
    public TileStack[] getValid(final int index) {
        Preconditions.checkPositionIndex(index, this.size - 1);
        return TileStack.validate(this.inv[index]);
    }

    @Override
    public void put(final int index, final TileStack tileStack) {
        Preconditions.checkPositionIndex(index, this.size - 1);
        if (this.disallowInvalid && tileStack != null && !tileStack.isValid()) {
            throw new IllegalArgumentException("This container does not allow invalid stacks");
        }
        this.inv[index] = null;
        add(tileStack);
    }

    @Override
    public TileStack[] getContent() {
        return this.inv;
    }

    private void sort() {
        this.sortOrder.sort(this.inv);
    }

    public SortOrder getSortOrder() {
        return this.sortOrder;
    }


    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<ContainerSlot> iterator() {
        return new Iterator<ContainerSlot>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < AutoSortedContainer.this.size;
            }

            @Override
            public ContainerSlot next() {
                return new ContainerSlot(this.index, AutoSortedContainer.this.inv[this.index++]);
            }
        };
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }


}
