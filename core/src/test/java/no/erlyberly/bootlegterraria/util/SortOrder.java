package no.erlyberly.bootlegterraria.util;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.inventory.AutoSortedContainer;
import no.erlyberly.bootlegterraria.inventory.TileStack;

import java.util.Arrays;
import java.util.Comparator;

/**
 * The orders to sort an {@link AutoSortedContainer}
 *
 * @author kheba
 */
public class SortOrder {

    /**
     * Sort by the name of the tile type
     */
    public static Comparator<TileStack> TT_NAME = Comparator.comparing(tileStack -> tileStack.getTileType().getName());

    /**
     * Sort by the amount of tiles in ascending order
     */
    public static Comparator<TileStack> AMOUNT_ASC = Comparator.comparingInt(TileStack::getAmount);

    /**
     * Sort by the amount of tiles in descending order
     */
    public static Comparator<TileStack> AMOUNT_DESC = AMOUNT_ASC.reversed();

    /**
     * @param a
     *     The array to be sort
     * @param reversed
     *     If the sorts should be reversed
     * @param sorts
     *     The comparators used to sort {@code a}, the first will be the first to compare
     */
    public static void sort(final TileStack[] a, final boolean reversed, final Comparator<TileStack>... sorts) {
        Preconditions.checkNotNull(a);
        Preconditions.checkNotNull(sorts);
        Preconditions.checkArgument(sorts.length > 0, "There must be at least one way to sort");

        //create a  comparator that is using all the comparators
        Comparator<TileStack> masterComp = (ts1, ts2) -> {
            for (final Comparator<TileStack> comp : sorts) {
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

        //reverse the comparator
        if (reversed) {
            masterComp = masterComp.reversed();
        }

        //finally sort the array
        Arrays.sort(a, masterComp);
    }
}
