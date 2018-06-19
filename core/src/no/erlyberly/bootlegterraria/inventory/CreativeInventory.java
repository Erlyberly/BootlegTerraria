package no.erlyberly.bootlegterraria.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.Iterator;
import java.util.List;

/**
 * A creative inventory where most of the methods from {@link Inventory} is not supported. Only {@link #holding()} is
 * implemented.
 */
public class CreativeInventory extends Inventory {

    private int curr;
    private TileStack currHolding;

    public CreativeInventory() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(final int amount) {
                return CreativeInventory.this.scrolled(amount);
            }
        });
        this.curr = 1;
        scrolled(-1); //make sure the held item is not null
    }

    private boolean scrolled(final int amount) {
        int next = this.curr + amount;

        if (next == 0) {
            next = TileType.TILE_TYPES;
        }
        else if (next == TileType.TILE_TYPES + 1) {
            next = 1;
        }

        this.curr = next % (TileType.TILE_TYPES + 1);
        final TileType tt = TileType.getTileTypeById(this.curr);
        this.currHolding = new TileStack(tt, 1);//(tt != null) ? new TileStack(tt, 1) : null;
        return true;
    }

    @Override
    public TileStack holding() {
        return this.currHolding;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public int firstEmpty() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int first(final TileStack tileStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<TileStack> add(final List<TileStack> tileStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAll(final TileType tileType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(final TileStack... tileStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(final TileStack tileStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAny(final TileType tileType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TileStack get(final int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TileStack[] getUnsafe(final int index, final boolean validate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(final int index, final TileStack tileStack) {
        throw new UnsupportedOperationException();
    }


    @Override
    public Iterator<TileStack> iterator() {
        throw new UnsupportedOperationException();
    }
}
