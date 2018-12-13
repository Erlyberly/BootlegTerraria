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
    }
}
