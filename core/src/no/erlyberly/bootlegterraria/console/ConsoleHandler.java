package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.LogLevel;
import no.erlyberly.bootlegterraria.GameMain;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ConsoleHandler {

    private final Console console;

    private static final DecimalFormat df = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.US));

    public ConsoleHandler() {
        this.console =
            new GUIConsole(new Skin(Gdx.files.internal("skins/l33t_skin/uiskin.json")), true, Input.Keys.APOSTROPHE);
        this.console.setCommandExecutor(new CommandHandler(this));

        this.console.log("INITIATING HACKING CONSOLE v0.96", LogLevel.ERROR);
        this.console
            .log("BOOTING SEQUENCE FINISHED IN " + ConsoleHandler.df.format(Math.random() * 4f), LogLevel.ERROR);

    }

    public void draw() {
        try {
            this.console.draw();
        } catch (final Exception ignore) {}
    }

    public boolean isConsoleVisible() {
        return this.console.isVisible();
    }

    public void log(final String msg) {
        log(msg, LogLevel.DEFAULT);
    }

    public void log(final String msg, final LogLevel level) {
        if (level == LogLevel.ERROR) {
            System.err.println(msg);
        }
        else {
            System.out.println(msg);
        }
        this.console.log(msg, level);
    }

    public void logf(final String msg, final Object... objs) {
        logf(msg, LogLevel.DEFAULT, objs);
    }

    public void logf(final String msg, final LogLevel level, final Object... objs) {
        if (level == LogLevel.ERROR) {
            System.err.printf(msg, objs);
        }
        else {
            System.out.printf(msg, objs);
        }
        this.console.log(String.format(msg, objs), level);
    }

    public void resetInputProcessing() {
        Gdx.input.setInputProcessor(GameMain.inst().getInputHandler());
        this.console.resetInputProcessing();
    }

    public void execCommand(final String command) {
        this.console.execCommand(command);
    }
}
