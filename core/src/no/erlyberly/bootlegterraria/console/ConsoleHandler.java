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

        log("INITIATING v#" + Util.getLastGitCommitID(false), LogLevel.SUCCESS);
    }

    @Override
    public void draw() {
        try {
            super.draw();
        } catch (final Exception ignore) {}
    }

    @Override
    public void log(final String msg, final LogLevel level) {
        if (level == LogLevel.ERROR) {
            System.err.println(msg);
        }
        else {
            System.out.println(msg);
        }
        super.log(msg, level);
    }

    public void logf(final String msg, final Object... objs) {
        logf(msg, LogLevel.DEFAULT, objs);
    }

    public void logf(final String msg, final LogLevel level, final Object... objs) {
        if (level == LogLevel.ERROR) {
            System.err.printf(msg + "\n", objs);
        }
        else {
            System.out.printf(msg + "\n", objs);
        }
        super.log(String.format(msg, objs), level);
    }
}
