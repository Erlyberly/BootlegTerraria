package no.erlyberly.bootlegterraria.render.ui;

import com.badlogic.gdx.Gdx;
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
    private float alpha;
    private float fadeTime; //in seconds


    /**
     * @param message
     *     The message to display
     * @param fadeTime
     *     How long it will take for the message to fade away
     */
    UIMessage(String message, float fadeTime) {
        this.fadeTime = fadeTime;
        style = new Label.LabelStyle();
        style.font = GameMain.map.font;
        label = new Label(message, style);

        alpha = 1;
        shown = true;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (shown || alpha > 0) {
            label.draw(batch, alpha);
            if (!shown) {
                alpha -= Gdx.graphics.getDeltaTime() / fadeTime;
                if (alpha <= 0) {
                    super.remove();
                }
            }
        }
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public boolean remove() {
        shown = false;
        return true;
    }
}
