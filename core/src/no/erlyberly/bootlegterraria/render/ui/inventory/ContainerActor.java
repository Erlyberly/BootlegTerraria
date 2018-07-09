package no.erlyberly.bootlegterraria.render.ui.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import no.erlyberly.bootlegterraria.storage.ContainerSlot;
import no.erlyberly.bootlegterraria.storage.IContainer;

/**
 * @author kheba
 */
public class ContainerActor extends Window {

    private final IContainer container;
    private final Skin skin;
    SlotActor[] slots;
    boolean update = false;

    public ContainerActor(final IContainer container, final DragAndDrop dragAndDrop, final Skin skin) {
        super(container.getName(), skin);
        this.container = container;
        this.skin = skin;
        setResizable(false);
        setMovable(false);

        this.slots = new SlotActor[container.getSize()];

        final TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new HidingClickListener(this));
        getTitleTable().add(closeButton).height(getPadTop());

        setPosition(Gdx.graphics.getWidth() / 2 - getWidth(), Gdx.graphics.getHeight() / 2);
        defaults().space(8);
        row().fill().expandX();

        int i = 0;
        for (final ContainerSlot cslot : container) {
            final SlotActor slotActor =
                new SlotActor(skin, new Slot(this, cslot.getContent(), cslot.getIndex()), container);
            this.slots[cslot.getIndex()] = slotActor;
            dragAndDrop.addSource(new SlotSource(slotActor));
            dragAndDrop.addTarget(new SlotTarget(slotActor));

            add(slotActor);

            i++;
            if (i % 10 == 0) {
                row();
            }
        }

        pack();

        //Hidden by default
        setVisible(false);

    }

    @Override
    public void act(final float delta) {
        super.act(delta);
        if (this.update) {
            for (final ContainerSlot cslot : this.container) {
                this.slots[cslot.getIndex()].update();
            }
            this.update = false;
        }
    }

    public void update() {
        this.update = true;
    }

    public IContainer getContainer() {
        return this.container;
    }
}
