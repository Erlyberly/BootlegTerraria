package no.erlyberly.bootlegterraria.render.ui.inventory;

import no.erlyberly.bootlegterraria.storage.TileStack;

/**
 * @author Daniel Holderbaum
 */
public class Slot {

    private final ContainerActor actor;
    private TileStack stack;
    private final int index;


    public Slot(final ContainerActor actor, final TileStack stack, final int index) {
        this.actor = actor;
        this.stack = stack;
        this.index = index;
    }

    public Slot(final Slot slot) {
        this(slot.actor, slot.stack, slot.index);
    }

    public ContainerActor getContainerActor() {
        return actor;
    }

    public int getIndex() {
        return index;
    }

    public boolean isEmpty() {
        return stack == null;
    }

    public TileStack getStack() {
        return stack;
    }

    public void updateStack() {
        stack = actor.getContainer().get(index);
    }
}
