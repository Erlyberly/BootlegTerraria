package no.erlyberly.bootlegterraria.inventory;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.Arrays;
import java.util.List;

/**
 * An interface for things that holds items.
 * <p>
 * If the container can only hold valid stacks (checked with {@link TileStack#isValid()}) is up to the implementation.
 * <p>
 * However for {@link #get(int)} the returned TileStack <bold>MUST</bold> be a valid stacks (or null) if this is not the
 * desired use {@link #getUnsafe(int, boolean)} for invalid stacks
 * <p>
 * TODO implement a container that allow for invalid stacks (a sort of main storage)
 * TODO implement a chest (that can be opened, somehow)
 * TODO let some entities have inventories
 *
 * @author kheba
 */
public interface TileContainer extends Iterable<Slot> {

    /**
     * @return How many slots this container holds
     */
    int getSize();

    /**
     * @return The first empty slot in the container, return a negative number if none is found
     */
    int firstEmpty();

    /**
     * @param tileType
     *     The tileType to match against
     *
     * @return The index of the first tile where there are at least {@code tileStack.getAmount} tiles
     *
     * @throws NullPointerException
     *     if the {@code tileType} is null
     */
    default int first(final TileType tileType) {
        Preconditions.checkNotNull(tileType);
        return first(new TileStack(tileType, 1));
    }

    /**
     * @param tileStack
     *     The tileStack to match against
     *
     * @return The index of the first tile where there are at least {@code tileStack.getAmount} tiles
     *
     * @throws NullPointerException
     *     if the {@code tileStack} is null
     */
    int first(TileStack tileStack);

    /**
     * Add an item to the container
     *
     * @throws IllegalArgumentException
     *     if one of the {@code tileStack}s is {@code null}
     */
    default void add(final TileType tileType, final int amount) {
        add(TileStack.stack(tileType, amount));
    }

    /**
     * Add one or more items to the container
     *
     * @param tileStack
     *     What to add
     *
     * @throws IllegalArgumentException
     *     if one of the {@code tileStack}s is {@code null}
     */
    default List<TileStack> add(final TileStack... tileStack) {
        return add(Arrays.asList(tileStack));
    }

    /**
     * Add one or more items to the container
     *
     * @param tileStack
     *     What to add
     *
     * @throws IllegalArgumentException
     *     if one of the {@code tileStack}s is {@code null}
     */
    List<TileStack> add(List<TileStack> tileStack);

    /**
     * Remove all tile stacks with the given tile type
     */
    void removeAll(TileType tileType);

    /**
     * Remove {@code amount} of the given tile type
     *
     * @param amount
     *     How many to remove
     * @param tileType
     *     What tile to remove
     *
     * @return How many tiles that were not removed
     */
    int remove(final TileType tileType, final int amount);

    /**
     * Remove tile stacks in the container that match the given element
     *
     * @param tileStack
     *     The tile stack to remove
     */
    void remove(TileStack tileStack);

    /**
     * Remove tile at index
     *
     * @throws IndexOutOfBoundsException
     *     if the index is less than 0 or greater than or equal to {@link #getSize()}
     */
    void remove(final int index);

    /**
     * Clear the container of all tiles
     */
    void clear();

    /**
     * @param tileStack
     *     The item to check for
     *
     * @return False if {@code tileStack} is null, true if this container has the given {@code tileStack}
     */
    boolean contains(TileStack tileStack);

    /**
     * @return If this container has the given {@code tileStack}, {@code false} is returned if tiletype is {@code
     * null}
     */
    default boolean contains(final TileType tileType, final int size) {
        if (tileType == null) {
            return false;
        }
        return contains(new TileStack(tileType, size));
    }


    /**
     * @return If the container has an item with this
     */
    boolean containsAny(TileType tileType);

    /**
     * @return The {@code TileStack} at the given location, {@code null} if there is nothing there
     *
     * @throws IndexOutOfBoundsException
     *     if the index is less than 0 or greater than or equal to {@link #getSize()}
     */
    TileStack get(final int index);

    /**
     * @param validate
     *     If this method should return a valid stack or not
     *
     * @return A tile stack at a given location, it will pass {@link TileStack#isValid()} if {@code validate} is true.
     *
     * @throws IndexOutOfBoundsException
     *     if the index is less than 0 or greater than or equal to {@link #getSize()}
     */
    TileStack[] getUnsafe(int index, boolean validate);

    /**
     * Overwrite a the given {@code tileStack} at {@code index}. If the given tile stack is {@code null}  it is the same
     * as calling {@link #remove(int)}.
     *
     * @param index
     *     The index to place the {@code tileStack} at
     * @param tileStack
     *     The tileStack to put at {@code index}
     *
     * @throws IndexOutOfBoundsException
     *     if the index is less than 0 or greater than or equal to {@link #getSize()}
     */
    void put(int index, TileStack tileStack);

    /**
     * @return The underlying array of the container
     */
    TileStack[] getContent();
}
