package no.erlyberly.bootlegterraria.storage.impl;

import com.google.common.base.Preconditions;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.ui.inventory.ContainerActor;
import no.erlyberly.bootlegterraria.storage.ContainerSlot;
import no.erlyberly.bootlegterraria.storage.IContainer;
import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author kheba
 */
public class Container implements IContainer {

    private String name;
    private final int size;
    private boolean validOnly;

    final TileStack[] cont;
    private ContainerActor actor;

    public static class ContainerBuilder {

        private String name = "Container";
        private int size;
        private boolean validOnly = true;


        public ContainerBuilder(int size) {
            this.size = size;
        }

        public Container build() {
            return new Container(name, size, validOnly);
        }

        public ContainerBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public ContainerBuilder setSize(int size) {
            this.size = size;
            return this;
        }

        public ContainerBuilder setValidOnly(boolean validOnly) {
            this.validOnly = validOnly;
            return this;
        }

        public String getName() {
            return name;
        }

        public int getSize() {
            return size;
        }

        public boolean isValidOnly() {
            return validOnly;
        }
    }

    public Container(final String name, final int size, boolean validOnly) {
        Preconditions.checkArgument(size > 0, "Inventory size must be greater than zero");
        this.name = name;
        this.size = size;
        this.validOnly = validOnly;
        cont = new TileStack[size];
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean isValidOnly() {
        return validOnly;
    }

    @Override
    public int firstEmpty() {
        for (int i = 0; i < size; i++) {
            if (cont[i] == null) { return i; }
        }
        return -1;
    }

    @Override
    public int first(final TileStack tileStack) {
        if (tileStack == null) {
            return firstEmpty();
        }
        // if invalid stacks is not allows this cannot have a invalid validate
        if (validOnly && !tileStack.isValid()) {
            return -1;
        }
        for (int i = 0; i < getSize(); i++) {
            final TileStack loopTs = cont[i];
            if (loopTs != null && loopTs.getTileType() == tileStack.getTileType() &&
                tileStack.getAmount() <= loopTs.getAmount()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int add(TileType tileType, int amount) {


        int index = first(tileType);
        if (index < 0) { index = firstEmpty(); }
        if (index < 0) { return amount; }
        else if (!validOnly) {
            TileStack ts = cont[index];
            if (ts == null) { cont[index] = new TileStack(tileType, amount); }
            else { ts.give(amount); }
            return 0;
        }

        //from here validOnly must be true, ie
        //assert (validOnly);

        for (TileStack stack : cont) {
            if (stack != null && stack.getTileType() == tileType && stack.getAmount() < tileType.getMaxStackSize()) {
                int needed = tileType.getMaxStackSize() - stack.getAmount();
                int given = Math.min(amount, needed);
                stack.give(given);
                amount -= given;
                if (amount == 0) { return 0; }
            }
        }

        TileStack[] validStacks = TileStack.validate(new TileStack(tileType, amount));

        int toSkip = -1;
        for (int i = 0, length = validStacks.length; i < length; i++) {
            index = firstEmpty();
            if (index < 0) {
                toSkip = i;
                break; //no more empty slots
            }
            cont[index] = validStacks[i];
        }

        //if we do not need to skip anything the loop finished successfully
        if (toSkip == -1) {
            return 0;
        }

        //skip the ones already added, then sum up the rest
        return Arrays.stream(validStacks).skip(toSkip).mapToInt(TileStack::getAmount).sum();
    }

    @Override
    public void removeAll(final TileType tileType) {
        for (int i = 0; i < size; i++) {
            if (cont[i] != null && cont[i].getTileType() == tileType) {
                cont[i] = null;
            }
        }
        updateContainer();
    }

    @Override
    public int remove(final TileType tileType, final int amount) {
        Preconditions.checkArgument(amount > 0, "The amount to remove must be positive, but was " + amount);
        int counter = amount;
        for (int i = 0, length = cont.length; i < length; i++) {
            if (cont[i] != null && tileType == cont[i].getTileType()) {
                final int newAmount = cont[i].getAmount() - counter;
                if (newAmount < 0) {
                    counter -= cont[i].getAmount();
                    cont[i] = null;
                }
                else {
                    if (newAmount != 0) { cont[i].setAmount(newAmount); }
                    else { cont[i] = null; }
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
        if (validOnly && !tileStack.isValid()) {
            return;
        }
        for (int i = 0, length = cont.length; i < length; i++) {
            if (tileStack.equals(cont[i])) {
                cont[i] = null;
            }
        }
        updateContainer();
    }

    @Override
    public void remove(final int index) {
        Preconditions.checkPositionIndex(index, size - 1);
        cont[index] = null;
        updateContainer();
    }

    @Override
    public void clear() {
        for (int i = 0; i < cont.length; i++) {
            cont[i] = null;
        }
        updateContainer();
    }

    @Override
    public boolean contains(final TileStack tileStack) {
        if (tileStack == null || (validOnly && !tileStack.isValid())) {
            return false;
        }
        for (final ContainerSlot slot : this) {
            if (tileStack.equals(slot.getContent())) {
                return true;
            }
        }
        return false;
    }

    //TODO implement
    @Override
    public boolean containsAny(final TileType tileType) {
        return false;
    }

    @Override
    public TileStack get(final int index) {
        Preconditions.checkPositionIndex(index, size - 1);
        return cont[index];
    }

    @Override
    public TileStack[] getValid(final int index) {
        Preconditions.checkPositionIndex(index, size - 1);
        return TileStack.validate(get(index));
    }

    @Override
    public void put(final int index, final TileStack tileStack) {
        Preconditions.checkPositionIndex(index, size - 1);
        if (validOnly && tileStack != null && !tileStack.isValid()) {
            throw new IllegalArgumentException("This container does not allow invalid stacks");
        }
        cont[index] = tileStack;
    }

    @Override
    public TileStack[] getContent() {
        return cont;
    }

    @Override
    public void updateContainer() {
        //might happen during tests or when the app is headless
        if (GameMain.ui == null) {
            return;
        }
        if (actor == null) {
            actor = GameMain.ui.getContainerActor(this);
        }
        actor.update();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<ContainerSlot> iterator() {
        return new Iterator<ContainerSlot>() {

            int index = 0;

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public ContainerSlot next() {
                return new ContainerSlot(index, cont[index++]);
            }
        };
    }
}
