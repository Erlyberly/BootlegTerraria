package no.erlyberly.bootlegterraria.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import no.erlyberly.bootlegterraria.GameMain;

/**
 * @author kheba
 */
public class InputHandler extends InputAdapter {

    public InputHandler() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean scrolled(final int amount) {
        if (amount < 0) {
            GameMain.inst().getGameMap().getPlayer().getInv().next();
            return true;
        }
        else if (amount > 0) {
            GameMain.inst().getGameMap().getPlayer().getInv().prev();
            return true;
        }
        return false;
    }
}
