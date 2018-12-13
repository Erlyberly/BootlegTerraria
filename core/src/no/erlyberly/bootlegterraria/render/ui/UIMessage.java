package no.erlyberly.bootlegterraria.render.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.util.SimpleDrawable;
import no.erlyberly.bootlegterraria.world.GameMap;

import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A threadsafe class to send messages to the screen.
 *
 * @author Elg
 */
public class UIMessage implements SimpleDrawable {

    private CopyOnWriteArrayList<Message> messages = new CopyOnWriteArrayList<>();
    private final GameMap map;

    public UIMessage(GameMap map) {

        this.map = map;
    }

    @Override
    public void draw(Batch batch) {
        messages.forEach(message -> message.draw(batch));
    }

    public static class Message {

        private final UUID uuid;
        private final Label.LabelStyle style;
        private final Label label;
        private final GameMap map;
        boolean shown;

        private Message(String message, GameMap map) {
            this.map = map;
            style = new Label.LabelStyle();
            style.font = map.font;
            label = new Label(message, style);

            shown = true;
            uuid = UUID.randomUUID();
        }

        public void draw(Batch batch) {
            if (isShown()) {
                getLabel().draw(batch, 1f);
            }
        }

        public void delete() {
            map.getUIMessage().messages.remove(this);
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

        public UUID getUuid() {
            return uuid;
        }

        public void delete(long timeLeftMs) {
            GameMain.SECONDARY_THREAD.schedule(this::delete, timeLeftMs);
        }
    }


    /**
     * @param text
     *     The text of the message
     *
     * @return The message created
     */
    public Message sendMessage(String text) {
        return sendMessage(text, 0);
    }

    /**
     * @param text
     *     The text of the message
     * @param x
     *     x coordinate of message
     * @param y
     *     y coordinate of message
     * @param width
     *     height of message
     * @param height
     *     height of message
     *
     * @return The message created
     */
    public Message sendMessage(String text, float x, float y, float width, float height) {
        return sendMessage(text, x, y, width, height, 0);
    }

    /**
     * @param text
     *     The text of the message
     * @param displayTimeMs
     *     If greater than 0, delete after given ms
     *
     * @return The message created
     */
    public Message sendMessage(String text, long displayTimeMs) {
        return sendMessage(text, (Gdx.graphics.getWidth() / 2f) - map.font.getSpaceWidth() * text.length() / 2,
                           Gdx.graphics.getHeight() / 4f, map.font.getSpaceWidth() * text.length(),
                           map.font.getCapHeight(), displayTimeMs);
    }

    /**
     * @param text
     *     The text of the message
     * @param x
     *     x coordinate of message
     * @param y
     *     y coordinate of message
     * @param width
     *     height of message
     * @param height
     *     height of message
     * @param displayTimeMs
     *     If greater than 0, delete after given ms
     *
     * @return The message created
     */
    public Message sendMessage(String text, float x, float y, float width, float height, long displayTimeMs) {
//        System.out.println(
//            "text = [" + text + "], x = [" + x + "], y = [" + y + "], width = [" + width + "], height = [" + height +
//            "], displayTimeMs = [" + displayTimeMs + "]");
        Message msg = new Message(text, map);
        messages.add(msg);
        msg.getLabel().setBounds(x, y, width, height);
        if (displayTimeMs > 0) {
            msg.delete(displayTimeMs);
        }
        GameMain.console.logf("<w: %s t: %d> %s ", map.getFileName(), displayTimeMs, text);
        return msg;
    }
}
