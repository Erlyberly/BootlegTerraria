package no.erlyberly.bootlegterraria.inventory;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author kheba
 */
public class AutoSortedInventory extends Inventory {

    private final int size;

    private TileStack[] inv;

    public AutoSortedInventory(final int size) {
        Preconditions.checkArgument(size > 0, "Inventory size must be greater than 0");
        this.size = size;
        this.inv = new TileStack[size];
    }

    @Override
    public TileStack holding() {
        return this.inv[getSel()];
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

        final ArrayList<TileStack> returnList = new ArrayList<>(tileStacks.size());

        for (int i = 0, size1 = tileStacks.size(); i < size1; i++) {
            final TileStack ts = tileStacks.get(i);
            if (ts == null || !ts.isValid()) {
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
    public void remove(final TileStack... tileStack) {

        sort();
    }

    @Override
    public void clear() {
        this.inv = new TileStack[this.size];
    }

    @Override
    public boolean contains(final TileStack tileStack) {
        if (tileStack == null) {
            return false;
        }
        for (final TileStack ts : this) {
            if (ts.equals(tileStack)) {
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
        Preconditions.checkPositionIndex(index, this.size - 1);
        return this.inv[index];
    }

    @Override
    public TileStack[] getUnsafe(final int index, final boolean validate) {
        Preconditions.checkPositionIndex(index, this.size - 1);
        return new TileStack[] {this.inv[index]};
    }

    @Override
    public void put(final int index, final TileStack tileStack) {
        Preconditions.checkPositionIndex(index, this.size - 1);
        this.inv[index] = tileStack;
        sort();
    }

    private void sort() {
        Arrays.sort(this.inv);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<TileStack> iterator() {
        return new Iterator<TileStack>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < AutoSortedInventory.this.size;
            }

            @Override
            public TileStack next() {
                return AutoSortedInventory.this.inv[this.index];
            }
        };
    }
}
