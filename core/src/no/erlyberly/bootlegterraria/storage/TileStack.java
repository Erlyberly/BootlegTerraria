package no.erlyberly.bootlegterraria.storage;

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

    /**
     * @param tt
     *     The type of tile this stack is made out of
     *
     * @throws NullPointerException
     *     if {@code tt} is null
     */
    public TileStack(final TileType tt) {
        this(tt, 1);
    }

    /**
     * @param tt
     *     The type of tile this stack is made out of
     * @param amount
     *     How many items that's in this stack, must be zero or greater
     *
     * @throws NullPointerException
     *     if {@code tt} is null
     * @throws IllegalArgumentException
     *     if {@code amount} is less than 0
     */
    public TileStack(final TileType tt, final int amount) {
        Preconditions.checkNotNull(tt, "The tile type cannot be null");
        this.tt = tt;
        setAmount(amount);
    }

    /**
     * @return The type of the stack this is
     */
    public TileType getTileType() {
        return tt;
    }

    /**
     * @return How big this stack is, will always be 0 or greater
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Set the amount this stack has,
     *
     * @param amount
     *     new amount, must be zero or greater
     *
     * @throws IllegalArgumentException
     *     if the amount is less than 0
     */
    public void setAmount(final int amount) {
        Preconditions.checkArgument(amount >= 0, "The amount of a stack cannot be negative: " + amount);
        this.amount = amount;
    }

    /**
     * Add the given amount to the size of the stack
     *
     * @param add
     *     How many items to add
     *
     * @throws IllegalArgumentException
     *     if {@code add} is less than 0
     */
    public void give(final int add) {
        Preconditions.checkArgument(add >= 0, "Amount added must be greater than or equal to 0");
        setAmount(amount + add);
    }

    /**
     * Remove a set amount. If the removed amount is greater than the amount currently in the stack the stack's size
     * will be set to zero
     *
     * @param remove
     *     How many items to remove
     *
     * @throws IllegalArgumentException
     *     if {@code remove} is less than 0
     */
    public void take(final int remove) {
        Preconditions.checkArgument(remove >= 0, "Amount removed must be greater than or equal to 0");
        setAmount(Math.max(amount - remove, 0));
    }

    /**
     * Split the given {@code tileStack} into valid stacks
     *
     * @param tileStack
     *     The validate to split
     *
     * @return An array of valid TileStacks
     */
    public static TileStack[] validate(final TileStack tileStack) {
        if (tileStack == null) {
            return null;
        }
        return validate(tileStack.getTileType(), tileStack.getAmount());
    }

    /**
     * Split {@code size} {@link TileType}s into valid stacks
     *
     * @param tt
     *     The type of tile
     * @param size
     *     How many of {@code tt} to split into stacks
     *
     * @return An array of valid TileStacks, {@code null} if {@code tt} is {@code null} or if {@code size} is less
     * than 0
     */
    public static TileStack[] validate(final TileType tt, final int size) {
        if (tt == null || size < 0) {
            return null;
        }
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

    /**
     * @return UI friendly name
     */
    public String displayName() {
        return tt.toString();
    }

    /**
     * @return If this stack is valid with regard to the {@link TileType#getMaxStackSize()}
     */
    public boolean isValid() {
        return amount >= 0 && amount <= tt.getMaxStackSize();
    }

    @Override
    public String toString() {
        return "TileStack{" + "tt=" + tt + ", amount=" + amount + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) { return true; }
        if (!(o instanceof TileStack)) { return false; }

        final TileStack stack = (TileStack) o;

        if (amount != stack.amount) { return false; }
        return tt == stack.tt;
    }

    @Override
    public int hashCode() {
        int result = tt.hashCode();
        result = 31 * result + amount;
        return result;
    }

    @Override
    public int compareTo(final TileStack o) {
        final int ttct = tt.compareTo(o.tt);
        if (ttct == 0) {
            return amount - o.amount;
        }
        return ttct;
    }
}
