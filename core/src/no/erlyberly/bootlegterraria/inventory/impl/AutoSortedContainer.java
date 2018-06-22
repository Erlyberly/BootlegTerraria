package no.erlyberly.bootlegterraria.inventory.impl;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.inventory.Slot;
import no.erlyberly.bootlegterraria.inventory.SortOrder;
import no.erlyberly.bootlegterraria.inventory.TileContainer;
import no.erlyberly.bootlegterraria.inventory.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An container that will auto sort the inventory when updated
 *
 * @author kheba
 */
public class AutoSortedContainer implements TileContainer {

    private final int size;
    private final boolean disallowInvalid;

    private final TileStack[] inv;

    private final SortOrder sortOrder;

    /**
     * A normal container that disallows invalid {@link TileStack}s and sorts first by name then by tile amount in an
     * descending order
     *
     * @param size
     *     The size of the container
     */
    public AutoSortedContainer(final int size) {
        //noinspection unchecked
        this(size, true);
    }

    /**
     * A container that sorts first by name then by tile amount in an descending order
     *
     * @param size
     *     The size of the container
     * @param disallowInvalid
     *     if this container does not allow invalid {@link TileStack}s
     */
    public AutoSortedContainer(final int size, final boolean disallowInvalid) {
        //noinspection unchecked
        this(size, disallowInvalid, new SortOrder(false, SortOrder.TT_NAME_ASC, SortOrder.AMOUNT_DESC));
    }

    /**
     * @param size
     *     The size of the container
     * @param disallowInvalid
     *     if this container does not allow invalid {@link TileStack}s
     * @param sortOrder
     *     The way to sort the stack each time it is modified
     */
    public AutoSortedContainer(final int size, final boolean disallowInvalid, final SortOrder sortOrder) {
        Preconditions.checkArgument(size > 0, "Inventory size must be greater than zero");
        Preconditions.checkNotNull(sortOrder, "The sort order cannot be null");

        this.disallowInvalid = disallowInvalid;
        this.size = size;
        this.sortOrder = sortOrder;

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
        Preconditions.checkNotNull(tileStack);

        // if invalid stacks is not allows this cannot have a invalid stack
        if (this.disallowInvalid && !tileStack.isValid()) {
            return -1;
        }
        for (int i = 0; i < this.size; i++) {
            final TileStack invTs = this.inv[i];
            if (invTs != null && invTs.getTileType() == tileStack.getTileType() &&
                invTs.getAmount() <= tileStack.getAmount()) {
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
                        this.inv[j].add(ts.getAmount());
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
//                System.out.println("adding " + ts + " to return list");
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
        for (final Slot slot : this) {
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

    //this implementation does not allow invalid stacks so this can be safely overwritten
    @Override
    public TileStack get(final int index) {

        if (this.disallowInvalid) {
            Preconditions.checkPositionIndex(index, this.size - 1);
            return this.inv[index];
        }

        final TileStack tt = getUnsafe(index, true)[0];
        if (tt != null && !tt.isValid()) {
            throw new IllegalStateException("The returned stack was not valid");
        }
        return tt;
    }

    @Override
    public TileStack[] getUnsafe(final int index, final boolean validate) {
        Preconditions.checkPositionIndex(index, this.size - 1);
        final TileStack ts = this.inv[index];
        if (ts == null) {
            return null;
        }

        if (validate) {
            return TileStack.stack(ts.getTileType(), ts.getAmount());
        }
        return new TileStack[] {ts};
    }

    @Override
    public void put(final int index, final TileStack tileStack) {
//        Preconditions.checkPositionIndex(index, this.size - 1);
//        if (this.disallowInvalid && tileStack != null && !tileStack.isValid()) {
//            throw new IllegalArgumentException("This container does not allow invalid stacks");
//        }
//        this.inv[index] = tileStack;
//        sort();
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

    @Override
    public Iterator<Slot> iterator() {
        return new Iterator<Slot>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < AutoSortedContainer.this.size;
            }

            @Override
            public Slot next() {
                return new Slot(this.index, AutoSortedContainer.this.inv[this.index++]);
            }
        };
    }
}
