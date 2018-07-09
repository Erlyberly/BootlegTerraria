package no.erlyberly.bootlegterraria.render.ui.inventory;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import no.erlyberly.bootlegterraria.storage.IContainer;
import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.storage.impl.AutoSortedContainer;

/**
 * @author Daniel Holderbaum
 */
public class SlotSource extends Source {

    private final Slot sourceSlot;

    public SlotSource(final SlotActor actor) {
        super(actor);
        this.sourceSlot = actor.getSlot();
    }

    @Override
    public Payload dragStart(final InputEvent event, final float x, final float y, final int pointer) {
        if (this.sourceSlot.getStack() == null || this.sourceSlot.getStack().getAmount() == 0 ||
            this.sourceSlot.getContainerActor().getContainer() instanceof AutoSortedContainer) {
            return null;
        }

        final Payload payload = new Payload();
        final Slot payloadSlot = new Slot(this.sourceSlot);
        final TileStack srcTs = this.sourceSlot.getStack();

//        srcTs.take(srcTs.getAmount());

        payload.setObject(payloadSlot);

        final TextureRegion icon = srcTs.getTileType().getTextureRegion();

        final Actor dragActor = new Image(icon);
        payload.setDragActor(dragActor);

        final Actor validDragActor = new Image(icon);
        // validDragActor.setColor(0, 1, 0, 1);
        payload.setValidDragActor(validDragActor);

        final Actor invalidDragActor = new Image(icon);
        // invalidDragActor.setColor(1, 0, 0, 1);
        payload.setInvalidDragActor(invalidDragActor);

        return payload;
    }

    @Override
    public void dragStop(final InputEvent event, final float x, final float y, final int pointer, final Payload payload,
                         final Target target) {
        final Slot payloadSlot = (Slot) payload.getObject();
        if (target != null) {
            final Slot targetSlot = ((SlotActor) target.getActor()).getSlot();

            final IContainer cont = targetSlot.getContainerActor().getContainer();

            if (targetSlot.getStack() == null) {
                cont.put(targetSlot.getIndex(), payloadSlot.getStack());
                cont.remove(payloadSlot.getIndex());
            }
            else if (targetSlot.getStack().getTileType() == payloadSlot.getStack().getTileType()) {
                final TileStack ts = cont.get(targetSlot.getIndex());
                ts.give(payloadSlot.getStack().getAmount());
                cont.put(targetSlot.getIndex(), ts);
                cont.remove(payloadSlot.getIndex());
            }
            else {
                //swap the two items
                final TileStack targetTs = cont.get(targetSlot.getIndex());
                final TileStack payloadTs = cont.get(payloadSlot.getIndex());

                cont.put(targetSlot.getIndex(), payloadTs);
                cont.put(payloadSlot.getIndex(), targetTs);
            }
        }
//        else {
//            this.sourceSlot.add(payloadSlot.getItem(), payloadSlot.getAmount());
//        }
    }
}

