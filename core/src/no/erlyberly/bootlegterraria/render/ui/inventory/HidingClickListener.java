package no.erlyberly.bootlegterraria.render.ui.inventory;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Its only purpose is to hide a given actor on click.
 *
 * @author Daniel Holderbaum
 */
public class HidingClickListener extends ClickListener {

    private final Actor actor;

    public HidingClickListener(final Actor actor) {
        this.actor = actor;
    }

    @Override
    public void enter(final InputEvent event, final float x, final float y, final int pointer, final Actor fromActor) {
        final Window window = (Window) actor;
        window.setMovable(false);
    }

    @Override
    public void exit(final InputEvent event, final float x, final float y, final int pointer, final Actor toActor) {
        final Window window = (Window) actor;
        window.setMovable(true);
    }

    @Override
    public void clicked(final InputEvent event, final float x, final float y) {
        actor.setVisible(false);
    }
}
