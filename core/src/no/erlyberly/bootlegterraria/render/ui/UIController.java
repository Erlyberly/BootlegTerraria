package no.erlyberly.bootlegterraria.render.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Disposable;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.render.ui.inventory.ContainerActor;
import no.erlyberly.bootlegterraria.storage.IContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kheba
 */
public class UIController implements Disposable {

    private final Stage stage;
    private final Skin skin;

    private final Map<IContainer, ContainerActor> containers;
    private DragAndDrop dad;

    public UIController() {
        skin = new Skin(Gdx.files.internal("skins/container_skin/uiskin.json"));
        stage = new Stage();
        containers = new HashMap<>();
        dad = new DragAndDrop();
        GameMain.inputMultiplexer.addProcessor(stage);
    }

    public void resize(final int width, final int height) {
        // See below for what true means.
        stage.getViewport().update(width, height, true);
    }

    /**
     * Register an IContainer with the UI, if one is already registered return a saved instance
     *
     * @return The ContainerActor for the given container
     */
    public ContainerActor getContainerActor(final IContainer container) {
        ContainerActor actor = containers.get(container);
        if (actor == null) {
            actor = new ContainerActor(container, dad, skin);
            stage.addActor(actor);
            containers.put(container, actor);
        }
        return actor;
    }


    public void render() {
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    /**
     * @param text
     *     The text of the message
     *
     * @return The message created
     */
    public UIMessage sendMessage(String text) {
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
    public UIMessage sendMessage(String text, float x, float y, float width, float height) {
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
    public UIMessage sendMessage(String text, long displayTimeMs) {
        return sendMessage(text, (Gdx.graphics.getWidth() / 2f) - GameMain.map.font.getSpaceWidth() * text.length() / 2,
                           Gdx.graphics.getHeight() / 4f, GameMain.map.font.getSpaceWidth() * text.length(),
                           GameMain.map.font.getCapHeight(), displayTimeMs);
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
    public UIMessage sendMessage(String text, float x, float y, float width, float height, long displayTimeMs) {
//        System.out.println(
//            "text = [" + text + "], x = [" + x + "], y = [" + y + "], width = [" + width + "], height = [" + height +
//            "], displayTimeMs = [" + displayTimeMs + "]");
        UIMessage msg = new UIMessage(text);
        stage.addActor(msg);
        msg.getLabel().setBounds(x, y, width, height);
        if (displayTimeMs > 0) {
            delete(msg, displayTimeMs);
        }
        GameMain.console.logf("<t: %d> %s", displayTimeMs, text);
        return msg;
    }

    public void deleteUIMessage(UIMessage message) {
        message.remove();
    }

    public void delete(UIMessage message, long timeLeftMs) {
        GameMain.SECONDARY_THREAD.scheduleAsync(() -> deleteUIMessage(message), timeLeftMs);
    }
}
