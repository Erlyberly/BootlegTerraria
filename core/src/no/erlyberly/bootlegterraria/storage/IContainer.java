package no.erlyberly.bootlegterraria.storage;

import no.erlyberly.bootlegterraria.world.TileType;

import java.util.Arrays;
import java.util.List;

/**
 * An interface for things that holds items.
 * <p>
 * If the container can only hold valid stacks (checked with {@link TileStack#isValid()}) is up to the implementation.
 * <p>
 * However for {@link #getValid(int)} the returned TileStack <bold>MUST</bold> be a valid stacks (or null) if this is
 * not the
 * desired use {@link #get(int)} for invalid stacks
 * <p>
 * TODO implement a chest (that can be opened, somehow)
 * TODO let some entities have inventories
 *
 * @author kheba
 */
public interface IContainer extends Iterable<ContainerSlot> {

    /**
     * @return The name of the container
     */
    String getName();

    /**
     * @param name
     *     The new name of the container
     */
    void setName(String name);

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
     * @return The index of the first tile where there are at least {@link TileStack#getAmount()} tiles. If the input is
     * null, this method is identical to {@link #firstEmpty()}
     */
    default int first(final TileType tileType) {
        if (tileType == null) {
            return firstEmpty();
        }
        return first(new TileStack(tileType, 0));
    }

    /**
     * x
     *
     * @param tileStack
     *     The tileStack to match against
     *
     * @return The index of the first tile where there are at least {@code tileStack.getAmount} tiles, return negative
     * number if not found. If the input is null, this method is identical to {@link #firstEmpty()}
     */
    int first(TileStack tileStack);

    /**
     * Add an item to the container
     *
     * @throws IllegalArgumentException
     *     if one of the {@code tileStack}s is {@code null} or if amount is less than zero
     */
    default void add(final TileType tileType, final int amount) {
        add(TileStack.validate(tileType, amount));
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
     *     The tile validate to remove
     *
     * @throws IllegalArgumentException
     *     if one of the {@code tileStack}s is {@code null}
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
     * Clear the container of all tile stacks
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
     * null} or if size is less than 0
     */
    default boolean contains(final TileType tileType, final int size) {
        if (tileType == null || size < 0) {
            return false;
        }
        return contains(new TileStack(tileType, size));
    }


    /**
     * @return If the container has an item of this TileType
     */
    boolean containsAny(TileType tileType);

    /**
     * This method returns the {@link TileStack} as is at the given location, there will be no check
     *
     * @return The {@code TileStack} at the given location, {@code null} if there is nothing there
     *
     * @throws IndexOutOfBoundsException
     *     if the index is less than 0 or greater than or equal to {@link #getSize()}
     */
    TileStack get(final int index);

    /**
     * @param index
     *     The index of the item to get
     *
     * @return An array of valid stacks (will pass {@link TileStack#isValid()}) from the tile stack at the given
     * location
     *
     * @throws IndexOutOfBoundsException
     *     if the index is less than 0 or greater than or equal to {@link #getSize()}
     */
    TileStack[] getValid(int index);

    /**
     * Overwrite a the given {@code tileStack} at {@code index}. If the given tile validate is {@code null} it is the
     * same as calling {@link #remove(int)}.
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

    /**
     * Update the GUI of the displayed container.
     */
    void updateContainer();

}
