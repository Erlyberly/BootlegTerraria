package no.erlyberly.bootlegterraria.render.ui.inventory;

import no.erlyberly.bootlegterraria.storage.TileStack;

/**
 * @author kheba
 */
public interface SlotListener {

    /**
     * Called when the item in the slot changed
     *
     * @param tileStack
     *     The new tileStack at the slot
     */
    void onChange(TileStack tileStack);
}
