package no.erlyberly.bootlegterraria.inventory;

import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.Comparator;

/**
 * The orders to sort an {@link AutoSortedContainer}
 *
 * @author kheba
 */
public final class SortOrder {

    private Comparator<TileStack> comp;
    private boolean rev = false;

    @SafeVarargs
    public SortOrder(final boolean reverse, final Comparator<TileStack>... sorts) {
        compileComparator(reverse, sorts);
    }

    private static final Comparator<TileStack> TT_NAME = Comparator.comparing(ts -> ts.getTileType().getName());

    /**
     * Sort descending by the name of the tile type with null at the end
     */
    public static Comparator<TileStack> TT_NAME_DESC = new Comparator<TileStack>() {
        @Override
        public int compare(final TileStack ts1, final TileStack ts2) {
            final int nullCmp = nullCmp(ts1, ts2);
            if (nullCmp != 0) {
                return nullCmp;
            }
            return ts1.getTileType().getName().compareTo(ts2.getTileType().getName());
        }

        @Override
        public Comparator<TileStack> reversed() {
            return TT_NAME_ASC;
        }
    };

    /**
     * Sort ascending by the name of the tile type with null at the end
     */
    public static Comparator<TileStack> TT_NAME_ASC = new Comparator<TileStack>() {
        @Override
        public int compare(final TileStack ts1, final TileStack ts2) {
            final int nullCmp = nullCmp(ts1, ts2);
            if (nullCmp != 0) {
                return nullCmp;
            }
            return -ts1.getTileType().getName().compareTo(ts2.getTileType().getName());
        }

        @Override
        public Comparator<TileStack> reversed() {
            return TT_NAME_DESC;
        }
    };

    /**
     * Sort by the amount of tiles in ascending order
     */
    public static Comparator<TileStack> AMOUNT_ASC = new Comparator<TileStack>() {
        @Override
        public int compare(final TileStack ts1, final TileStack ts2) {
            final int nullCmp = nullCmp(ts1, ts2);
            if (nullCmp != 0) {
                return nullCmp;
            }
            return ts1.getAmount() - ts2.getAmount();
        }

        @Override
        public Comparator<TileStack> reversed() {
            return AMOUNT_DESC;
        }
    };

    /**
     * Sort by the amount of tiles in descending order
     */
    public static Comparator<TileStack> AMOUNT_DESC = new Comparator<TileStack>() {
        @Override
        public int compare(final TileStack ts1, final TileStack ts2) {
            final int nullCmp = nullCmp(ts1, ts2);
            if (nullCmp != 0) {
                return nullCmp;
            }
            return ts2.getAmount() - ts1.getAmount();
        }

        @Override
        public Comparator<TileStack> reversed() {
            return AMOUNT_ASC;
        }
    };

    private static int nullCmp(final Object o1, final Object o2) {
        if (o1 == null) {
            return 1;
        }
        else if (o2 == null) {
            return -1;
        }
        else {
            return 0;
        }
    }

    /**
     * @param reverse
     *     If the sorts should be reversed
     * @param sorts
     *     The comparators used to sort {@code a}, the first will be the first to compare
     */
    @SafeVarargs
    public final void compileComparator(final boolean reverse, final Comparator<TileStack>... sorts) {
        Preconditions.checkNotNull(sorts);
        Preconditions.checkArgument(sorts.length > 0, "There must be at least one way to sort");

        //create a  comparator that is using all the comparators
        final Comparator<TileStack> masterComp = (ts1, ts2) -> {
            for (final Comparator<TileStack> comp : sorts) {
                if (comp == null) {
                    throw new IllegalArgumentException("None of the comparators can be null");
                }
                final int result = comp.compare(ts1, ts2);

                //if using this comparator is equal, use the next comparator
                if (result == 0) {
                    continue;
                }
                return result;
            }
            //everything is equal
            return 0;
        };

        this.comp = masterComp;

        //reverse the comparator
        if (reverse) {
            reverse();
        }
    }

    /**
     * Reverse the current sort order
     */
    public void reverse() {
        this.rev = !this.rev;
        this.comp = this.comp.reversed();
    }

    /**
     * @return If the current sort order is reversed
     */
    public boolean isReversed() {
        return this.rev;
    }

    /**
     * Sort the array with the given settings
     *
     * @param a
     *     The array to sort
     */
    public final void sort(final TileStack[] a) {
        Preconditions.checkNotNull(a);


        //finally sort the array using the compiled settings
        Arrays.sort(a, this.comp);
    }

    /**
     * Sort the given array with the given settings. This is the same as declaring a new SortOrder with the given
     * settings and calling sort on {@code a}
     *
     * @param a
     *     The array to sort
     * @param reversed
     *     If the sort order should be reversed
     * @param sorts
     *     The comparators to sort the array
     */
    public static void sort(final TileStack[] a, final boolean reversed, final Comparator<TileStack>... sorts) {
        new SortOrder(reversed, sorts).sort(a);
    }
}
