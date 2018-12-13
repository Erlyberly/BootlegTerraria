package no.erlyberly.bootlegterraria.render.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import no.erlyberly.bootlegterraria.GameMain;

/**
 * A threadsafe class to send messages to the screen.
 *
 * @author Elg
 */
public class UIMessage extends Actor {

    private final Label.LabelStyle style;
    private final Label label;
    private boolean shown;

    UIMessage(String message) {
        style = new Label.LabelStyle();
        style.font = GameMain.map.font;
        label = new Label(message, style);

        shown = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (isShown()) {
            getLabel().draw(batch, parentAlpha);
        }
    }

    public void setShown(boolean shown) {
        this.shown = shown;
    }

    public boolean isShown() {
        return shown;
    }

    public Label getLabel() {
        return label;
    }

    public Label.LabelStyle getStyle() {
        return style;
    }

}
