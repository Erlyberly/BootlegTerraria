package no.erlyberly.bootlegterraria.inventory;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.ArrayList;

/**
 * A stack of tiles that can be placed in a map
 *
 * @author kheba
 */
public class TileStack implements Comparable<TileStack> {

    private final TileType tt;
    private int amount;

    public TileStack(final TileType tt) {
        this(tt, 1);
    }

    /**
     * @param tt
     *     The type of tile this stack is made out of
     * @param amount
     *     How many items that's in this stack, must be zero or greater
     */
    public TileStack(final TileType tt, final int amount) {
        Preconditions.checkNotNull(tt, "The tile type cannot be null");
        this.tt = tt;
        setAmount(amount);
    }

    public TileType getTileType() {
        return this.tt;
    }

    public int getAmount() {
        return this.amount;
    }

    /**
     * Set the amount this stack has,
     *
     * @param amount
     *     new amount, must be zero or greater
     */
    public void setAmount(final int amount) {
        Preconditions.checkArgument(amount >= 0, "The amount of a stack cannot be negative: " + amount);
        this.amount = amount;
    }

    public void add(final int add) {
        setAmount(this.amount + add);
    }

    /**
     * Remove a set amount
     *
     * @param remove
     *     How many items to remove
     */
    public void remove(final int remove) {
        Preconditions.checkArgument(remove >= 0, "Amount removed must be greater than or equal to 0");
        setAmount(this.amount - remove);
    }

    /**
     * Split {@code size} {@link TileType}s into valid stacks
     *
     * @param tt
     *     The type of tile
     * @param size
     *     How many of {@code tt} to split into stacks
     *
     * @return An array of valid TileStacks
     */
    public static TileStack[] stack(final TileType tt, final int size) {
        final int ss = tt.getMaxStackSize();
        int currSize = size;

        final ArrayList<TileStack> stacks = new ArrayList<>();

        while (currSize > 0) {
            int stackSize = currSize - ss;
            if (stackSize <= 0) {
                stackSize = currSize;
            }
            stacks.add(new TileStack(tt, stackSize));
            currSize -= stackSize;
        }
        return stacks.toArray(new TileStack[0]);
    }

    public String displayName() {
        return this.tt.toString();
    }

    /**
     * @return If this stack is valid with regard to the {@link TileType#getMaxStackSize()}
     */
    public boolean isValid() {
        return this.amount >= 0 && this.amount <= this.tt.getMaxStackSize();
    }

    @Override
    public String toString() {
        return "TileStack{" + "tt=" + this.tt + ", amount=" + this.amount + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof TileStack)) { return false; }

        final TileStack stack = (TileStack) o;

        if (this.amount != stack.amount) { return false; }
        return this.tt == stack.tt;
    }

    @Override
    public int hashCode() {
        int result = this.tt.hashCode();
        result = 31 * result + this.amount;
        return result;
    }

    @Override
    public int compareTo(final TileStack o) {
        final int ttct = this.tt.compareTo(o.tt);
        if (ttct == 0) {
            return this.amount - o.amount;
        }
        return ttct;
    }
}
