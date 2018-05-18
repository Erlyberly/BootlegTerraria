package no.erlyberly.bootlegterraria.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import no.erlyberly.bootlegterraria.world.TileType;

public class CreativeInventory {

    private int curr = 0;

    public CreativeInventory() {
        InputAdapter inputAdapter = new InputAdapter() {
            @Override
            public boolean scrolled(int amount) {
                int next = curr + amount;
                if (curr == 0 && amount == -1) {
                    next = TileType.TILE_TYPES;
                }
                curr = next % (TileType.TILE_TYPES + 1);
                return true;
            }
        };
        Gdx.input.setInputProcessor(inputAdapter);
    }


    public TileType getSelectedTileType() {
        return TileType.getTileTypeById(curr);
    }

    public String getSelectedTileTypeAsString() {
        TileType tt = getSelectedTileType();
        return tt == null ? "Air" : tt.toString();
    }

}
