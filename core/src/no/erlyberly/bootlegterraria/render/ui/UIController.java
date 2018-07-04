package no.erlyberly.bootlegterraria.render.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import no.erlyberly.bootlegterraria.GameMain;

/**
 * @author kheba
 */
public class UIController implements Disposable {

    private final Stage stage;
    Table table;

    public UIController() {
        this.stage = new Stage();
        GameMain.inst().getInputMultiplexer().addProcessor(this.stage);

        this.table = new Table();
        this.table.setDebug(true);
        this.table.setFillParent(true);
//        this.table.add("text 123123123");
        this.stage.addActor(this.table);
    }

    public void render() {
        this.stage.act(Gdx.graphics.getDeltaTime());
        this.stage.draw();
    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }
}
