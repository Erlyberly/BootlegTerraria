package no.erlyberly.bootlegterraria.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import no.erlyberly.bootlegterraria.world.TileType;

public class CreativeInventory {

    private int curr = 0;

    public CreativeInventory() {
        final InputAdapter inputAdapter = new InputAdapter() {
            @Override
            public boolean scrolled(final int amount) {
                int next = CreativeInventory.this.curr + amount;
                if (CreativeInventory.this.curr == 0 && amount == -1) {
                    next = TileType.TILE_TYPES;
                }
                CreativeInventory.this.curr = next % (TileType.TILE_TYPES + 1);
                return true;
            }
        };
        Gdx.input.setInputProcessor(inputAdapter);
    }


    public TileType getSelectedTileType() {
        return TileType.getTileTypeById(this.curr);
    }

    public String getSelectedTileTypeAsString() {
        final TileType tt = getSelectedTileType();
        return tt == null ? "Air" : tt.toString();
    }

}
