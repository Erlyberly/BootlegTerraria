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
                curr = (curr + 1) % (TileType.TILE_TYPES + 1);
                return true;
            }
        };
        Gdx.input.setInputProcessor(inputAdapter);
    }


    public TileType getSelectedTileType() {
        return TileType.getTileTypeById(curr);
    }


}
