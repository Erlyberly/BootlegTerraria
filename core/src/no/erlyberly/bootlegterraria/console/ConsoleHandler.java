package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.LogLevel;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ConsoleHandler {

    private final Console console;

    private static final DecimalFormat df = new DecimalFormat("#.###", DecimalFormatSymbols.getInstance(Locale.US));

    public ConsoleHandler() {
        this.console =
            new GUIConsole(new Skin(Gdx.files.internal("skins/l33t_skin/uiskin.json")), true, Input.Keys.APOSTROPHE);
        this.console.setCommandExecutor(new CommandHandler());

        this.console.log("INITIATING HACKING CONSOLE v0.96", LogLevel.ERROR);
        this.console.log("BOOTING SEQUENCE FINISHED IN " + df.format(Math.random() * 4f), LogLevel.ERROR);

    }

    public void draw() {
        this.console.draw();
    }

    public boolean isConsoleVisible() {
        return this.console.isVisible();
    }

    public void log(final String msg) {
        this.console.log(msg);
    }

    public void log(final String msg, final Object... objs) {
        this.console.log(String.format(msg, objs));
    }

    public void log(final String msg, final LogLevel level) {
        this.console.log(msg, level);
    }

    public void log(final String msg, final LogLevel level, final Object... objs) {
        this.console.log(String.format(msg, objs), level);
    }
}
