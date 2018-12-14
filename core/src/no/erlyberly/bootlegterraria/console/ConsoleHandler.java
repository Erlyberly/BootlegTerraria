package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.LogLevel;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.util.Util;
import no.kh498.util.Reflection;

public class ConsoleHandler extends GUIConsole {

    private Window consoleWindow;

    public ConsoleHandler() {
        super(new Skin(Gdx.files.internal("skins/l33t_skin/uiskin.json")), false, Input.Keys.APOSTROPHE);
        setCommandExecutor(new CommandHandler(this));
        GameMain.inputMultiplexer.addProcessor(getInputProcessor());
        logToSystem = true;
        consoleTrace = true;

        try {
            consoleWindow = (Window) Reflection.getSuperField(this, "consoleWindow");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        log("INITIATING #" + Util.getLastGitCommitID(false), LogLevel.SUCCESS);
    }

    public void setAlpha(float a) {
        Color color = consoleWindow.getColor();
        color.a = a;
        consoleWindow.setColor(color);
    }

    public float getAlpha() {
        return consoleWindow.getColor().a;
    }

    public void logf(final String msg, final Object... objs) {
        logf(LogLevel.DEFAULT, msg, objs);
    }

    public void logf(final LogLevel level, final String msg, final Object... objs) {
        super.log(String.format(msg, objs), level);
    }
}
