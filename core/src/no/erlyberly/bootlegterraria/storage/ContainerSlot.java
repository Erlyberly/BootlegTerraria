package no.erlyberly.bootlegterraria.storage;

/**
 * A slot in in an container
 *
 * @author kheba
 */
public class ContainerSlot {

    private final int index;
    private final TileStack content;

    public ContainerSlot(final int index, final TileStack content) {
        this.index = index;
        this.content = content;
    }

    public int getIndex() {
        return index;
    }

    public TileStack getContent() {
        return content;
    }
}
