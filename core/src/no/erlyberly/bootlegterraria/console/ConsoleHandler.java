package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.LogLevel;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.util.Util;

public class ConsoleHandler extends GUIConsole {

    public ConsoleHandler() {
        super(new Skin(Gdx.files.internal("skins/l33t_skin/uiskin.json")), false, Input.Keys.APOSTROPHE);
        setCommandExecutor(new CommandHandler(this));
        GameMain.inputMultiplexer.addProcessor(getInputProcessor());
        this.logToSystem = true;
        this.consoleTrace = true;

        log("INITIATING #" + Util.getLastGitCommitID(false), LogLevel.SUCCESS);
    }

    public void logf(final String msg, final Object... objs) {
        logf(LogLevel.DEFAULT, msg, objs);
    }

    public void logf(final LogLevel level, final String msg, final Object... objs) {
        super.log(String.format(msg, objs), level);
    }
}
