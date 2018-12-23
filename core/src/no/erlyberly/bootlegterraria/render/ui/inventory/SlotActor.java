package no.erlyberly.bootlegterraria.render.ui.inventory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.storage.IContainer;
import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;

/**
 * @author kheba
 */
public class SlotActor extends ImageButton {

    private static final TextureRegion EMPTY;

    static {
        if (!GameMain.isHeadless()) {
            final Pixmap pixmap = new Pixmap((int) TileType.TILE_SIZE, (int) TileType.TILE_SIZE, Pixmap.Format.Alpha);
            EMPTY = new TextureRegion(new Texture(pixmap));
        }
        else { EMPTY = null; }
    }

    private final Skin skin;

    private final Slot slot;
    private final IContainer container;

    SlotActor(final Skin skin, final Slot slot, final IContainer container) {
        super(createStyle(skin, slot.getStack()));
        this.slot = slot;
        this.container = container;
        this.skin = skin;

        //TODO tooltip
//        final SlotTooltip tooltip = new SlotTooltip(slot, skin);
//        tooltip.setTouchable(
//            Touchable.disabled); // allows for mouse to hit tooltips in the top-right corner of the screen without
//        // flashing
//        InventoryScreen.stage.addActor(tooltip);
//        addListener(new TooltipListener(tooltip, true));
    }

    private static ImageButtonStyle createStyle(final Skin skin, final TileStack tileStack) {
        final TextureRegion image;
        if (tileStack != null) { image = tileStack.getTileType().getTextureRegion(); }
        else { image = EMPTY; }

        final ImageButtonStyle style = new ImageButtonStyle(skin.get(ButtonStyle.class));

        Sprite sprite = new Sprite(image);

        sprite.setSize(sprite.getHeight() * 4, sprite.getWidth() * 4);

        style.imageUp = new SpriteDrawable(sprite);
        style.imageDown = new SpriteDrawable(sprite);

        return style;
    }

    public void update() {
        slot.updateStack();
        setStyle(createStyle(skin, slot.getStack()));
    }

    public Slot getSlot() {
        return slot;
    }
}
