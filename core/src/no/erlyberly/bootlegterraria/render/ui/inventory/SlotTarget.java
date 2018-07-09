package no.erlyberly.bootlegterraria.render.ui.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

/**
 * @author Daniel Holderbaum
 */
public class SlotTarget extends Target {

    private final Slot targetSlot;

    public SlotTarget(final SlotActor actor) {
        super(actor);
        this.targetSlot = actor.getSlot();
        getActor().setColor(Color.LIGHT_GRAY);
    }

    @Override
    public boolean drag(final Source source, final Payload payload, final float x, final float y, final int pointer) {
        final Slot payloadSlot = (Slot) payload.getObject();
        // if (targetSlot.getItem() == payloadSlot.getItem() ||
        // targetSlot.getItem() == null) {
        getActor().setColor(Color.WHITE);
        return true;
        // } else {
        // getActor().setColor(Color.DARK_GRAY);
        // return false;
        // }
    }

    @Override
    public void drop(final Source source, final Payload payload, final float x, final float y, final int pointer) {
    }

    @Override
    public void reset(final Source source, final Payload payload) {
        getActor().setColor(Color.LIGHT_GRAY);
    }

}
