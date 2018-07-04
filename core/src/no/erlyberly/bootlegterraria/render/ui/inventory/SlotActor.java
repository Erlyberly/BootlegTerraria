package no.erlyberly.bootlegterraria.render.ui.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import no.erlyberly.bootlegterraria.storage.TileStack;

/**
 * @author kheba
 */
public class SlotActor extends ImageButton implements SlotListener {

    TileStack tileStack;
    Skin skin;

    public SlotActor(final Skin skin, final TileStack tileStack) {
        super(skin);
//        super(createStyle(skin, tileStack));
//
//        createS this.skin = skin;
        this.tileStack = tileStack;
    }

    @Override
    public void onChange(final TileStack tileStack) {

    }
}
