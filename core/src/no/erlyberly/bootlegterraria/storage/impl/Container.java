package no.erlyberly.bootlegterraria.storage.impl;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.ui.inventory.ContainerActor;
import no.erlyberly.bootlegterraria.storage.ContainerSlot;
import no.erlyberly.bootlegterraria.storage.IContainer;
import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author kheba
 */
public class Container implements IContainer {

    private String name;
    private final int size;

    private final TileStack[] cont;
    private ContainerActor actor;

    public Container(final int size) {
        this("Container", size);
    }

    public Container(final String name, final int size) {

        this.name = name;
        this.size = size;

        this.cont = new TileStack[size];
    }


    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int firstEmpty() {
        for (int i = 0; i < this.size; i++) {
            if (this.cont[i] == null) { return i; }
        }
        return -1;
    }

    @Override
    public int first(final TileStack tileStack) {
        if (tileStack == null) {
            return firstEmpty();
        }
        for (int i = 0; i < this.size; i++) {
            if (this.cont[i] != null && this.cont[i].equals(tileStack)) { return i; }
        }
        return -1;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public List<TileStack> add(final List<TileStack> tileStackList) {
        Preconditions.checkNotNull(tileStackList, "Cannot add a null list");

        final List<TileStack> returnList = new ArrayList<>(tileStackList.size());

        for (int i = 0, size1 = tileStackList.size(); i < size1; i++) {
            final TileStack ts = tileStackList.get(i);
            if (ts == null) {
                throw new IllegalArgumentException("Element nr " + i + " is null");
            }

            boolean added = false;

            for (int j = 0; j < this.size; j++) {
                if (this.cont[j] != null && this.cont[j].getTileType() == ts.getTileType()) {
                    if (this.cont[j].getAmount() + ts.getAmount() <= ts.getTileType().getMaxStackSize()) {
                        this.cont[j].give(ts.getAmount());
                        added = true;
                        break;
                    }
                }
            }
            //add item to the list at the first empty position
            if (!added) {
                final int firstEmpty = firstEmpty();
                if (firstEmpty >= 0) {
                    this.cont[firstEmpty] = ts;
                    added = true;
                }
            }
            if (!added) {
                returnList.add(ts);
            }
        }
        updateContainer();
        return returnList;
    }

    @Override
    public void removeAll(final TileType tileType) {
        for (int i = 0; i < this.size; i++) {
            if (this.cont[i] != null && this.cont[i].getTileType() == tileType) {
                this.cont[i] = null;
            }
        }
        updateContainer();
    }

    @Override
    public int remove(final TileType tileType, final int amount) {
        Preconditions.checkArgument(amount > 0, "The amount to remove must be positive, but was " + amount);
        int counter = amount;
        for (int i = 0, length = this.cont.length; i < length; i++) {
            if (this.cont[i] != null && tileType == this.cont[i].getTileType()) {
                final int newAmount = this.cont[i].getAmount() - counter;
                if (newAmount < 0) {
                    counter -= this.cont[i].getAmount();
                    this.cont[i] = null;
                }
                else {
                    if (newAmount != 0) { this.cont[i].setAmount(newAmount); }
                    else { this.cont[i] = null; }
                    updateContainer();
                    return 0;
                }
            }
        }
        updateContainer();
        return counter;
    }

    @Override
    public void remove(final TileStack tileStack) {
        Preconditions.checkNotNull(tileStack, "cannot remove a null element");
        for (int i = 0, length = this.cont.length; i < length; i++) {
            if (tileStack.equals(this.cont[i])) {
                this.cont[i] = null;
            }
        }
        updateContainer();
    }

    @Override
    public void remove(final int index) {
        this.cont[index] = null;
        updateContainer();
    }

    @Override
    public void clear() {
        for (int i = 0; i < this.cont.length; i++) {
            this.cont[i] = null;
        }
        updateContainer();
    }

    @Override
    public boolean contains(final TileStack tileStack) {
        return false;
    }

    @Override
    public boolean containsAny(final TileType tileType) {
        return false;
    }

    @Override
    public TileStack get(final int index) {
        return this.cont[index];
    }

    @Override
    public TileStack[] getValid(final int index) {
        return TileStack.validate(get(index));
    }

    @Override
    public void put(final int index, final TileStack tileStack) {
        this.cont[index] = tileStack;
        updateContainer();
    }

    @Override
    public TileStack[] getContent() {
        return this.cont;
    }

    @Override
    public void updateContainer() {
        //might happen during tests or when the app is headless
        if (GameMain.ui == null) {
            return;
        }
        if (this.actor == null) {
            this.actor = GameMain.ui.getContainerActor(this);
        }
        this.actor.update();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<ContainerSlot> iterator() {
        return new Iterator<ContainerSlot>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return this.index < Container.this.size;
            }

            @Override
            public ContainerSlot next() {
                return new ContainerSlot(this.index, Container.this.cont[this.index++]);
            }
        };
    }
}
