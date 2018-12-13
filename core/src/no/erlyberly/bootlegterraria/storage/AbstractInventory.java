package no.erlyberly.bootlegterraria.storage;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.Entity;
import no.erlyberly.bootlegterraria.render.ui.inventory.ContainerActor;

/**
 * An abstracted storage that handles what item is selected
 *
 * @author kheba
 */
public abstract class AbstractInventory implements IInventory {

    private final Entity holder;
    private int sel;
    private ContainerActor actor;

    public AbstractInventory(final Entity holder) {
        this.holder = holder;
    }

    @Override
    public void next() {
        sel = (sel + 1) % getSize();
    }

    @Override
    public void prev() {
        if (sel == 0) {
            sel = getSize() - 1;
            return;
        }
        sel = (sel - 1) % getSize();
    }

    @Override
    public void open() {
        if (actor == null) {
            actor = GameMain.ui.getContainerActor(getContainer());
        }
        actor.setVisible(true);
    }

    @Override
    public void close() {
        if (actor == null) {
            return;
        }
        actor.setVisible(false);
    }

    @Override
    public boolean isOpen() {
        if (actor == null) {
            return false;
        }
        return actor.isVisible();
    }

    @Override
    public void select(final int index) {
        sel = Preconditions.checkPositionIndex(index, getSize() - 1);
    }

    @Override
    public int getSel() {
        return sel;
    }

    @Override
    public Entity getHolder() {
        return holder;
    }
}
