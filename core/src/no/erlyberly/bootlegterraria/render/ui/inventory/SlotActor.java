package no.erlyberly.bootlegterraria.render.ui.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.storage.IContainer;
import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.Objects;
/**
 * @author kheba
 */
public class SlotActor extends ImageButton {

    private static final Sprite EMPTY;

    private static final int SCALE = 4;

    static {
        if (!GameMain.isHeadless()) {
            final Pixmap pixmap =
                new Pixmap((int) TileType.TILE_SIZE * SCALE, (int) TileType.TILE_SIZE * SCALE, Pixmap.Format.Alpha);
            EMPTY = new Sprite(new Texture(pixmap));
        }
        else { EMPTY = null; }
    }

    private final Skin skin;
    private final Slot slot;
    private final IContainer container;
    private TileStack oldStack;

    SlotActor(final Skin skin, final Slot slot, final IContainer container) {
        super(createStyle(skin, slot.getStack()));
        this.slot = slot;
        this.container = container;
        this.skin = skin;

        oldStack = slot.getStack() == null ? null : slot.getStack().clone();

        //TODO tooltip
//        final SlotTooltip tooltip = new SlotTooltip(slot, skin);
//        tooltip.setTouchable(
//            Touchable.disabled); // allows for mouse to hit tooltips in the top-right corner of the screen without
//        // flashing
//        InventoryScreen.stage.addActor(tooltip);
//        addListener(new TooltipListener(tooltip, true));
    }

    private static ImageButtonStyle createStyle(final Skin skin, final TileStack tileStack) {
        final Sprite sprite;
        if (tileStack != null) {
            //draw everything to this buffer to overlap the stacks texture with how many there are in the stack
            FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA4444, (int) TileType.TILE_SIZE * SCALE,
                                                      (int) TileType.TILE_SIZE * SCALE, false);
            SpriteBatch batch = new SpriteBatch(10);

            //this is the region that will store the combined texture
            TextureRegion tex = new TextureRegion(frameBuffer.getColorBufferTexture());

            // the sprite _must_ be the size of the given graphics size
            Sprite rawSprite = new Sprite(tileStack.getTileType().getTextureRegion());
            rawSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            //Start drawing everything to the framebuffer
            frameBuffer.begin();

            //clear junk that might appear behind transparent textures3
            Gdx.gl.glClear(GL20.GL_COLOR_CLEAR_VALUE);

            //start the batch (as you would normally)
            batch.begin();

            //draw the tile texture
            rawSprite.draw(batch);


            //scale the font so we can read it
            BitmapFont font = GameMain.map.font;
            //remember old font scale
            float oldScaleX = font.getData().scaleX;
            float oldScaleY = font.getData().scaleY;
            font.getData().setScale(20, 12);

            //draw the amount in the lower left corner
            font.draw(batch, tileStack.getAmount() + "", 0, font.getCapHeight());

            //reset font scale
            font.getData().setScale(oldScaleX, oldScaleY);

            batch.end();
            frameBuffer.end();


            tex.flip(false, true);
            sprite = new Sprite(tex);
        }
        else { sprite = EMPTY; }

        final ImageButtonStyle style = new ImageButtonStyle(skin.get(ButtonStyle.class));

        style.imageUp = new SpriteDrawable(sprite);
        style.imageDown = new SpriteDrawable(sprite);

        return style;
    }

    public void update() {
        slot.updateStack();

        if (Objects.equals(slot.getStack(), oldStack)) {
            return;
        }
        oldStack = slot.getStack() == null ? null : slot.getStack().clone();

        setStyle(createStyle(skin, slot.getStack()));
    }

    public Slot getSlot() {
        return slot;
    }
}
