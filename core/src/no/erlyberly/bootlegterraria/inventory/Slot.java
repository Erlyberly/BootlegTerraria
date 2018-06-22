package no.erlyberly.bootlegterraria.inventory;

/**
 * A slot in in an inventory
 *
 * @author kheba
 */
public class Slot {

    private final int index;
    private final TileStack content;

    public Slot(final int index, final TileStack content) {
        this.index = index;
        this.content = content;
    }

    public int getIndex() {
        return this.index;
    }

    public TileStack getContent() {
        return this.content;
    }
}
