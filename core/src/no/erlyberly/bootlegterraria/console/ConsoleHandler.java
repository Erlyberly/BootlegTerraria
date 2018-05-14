package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import com.strongjoshua.console.LogLevel;
import com.strongjoshua.console.annotation.ConsoleDoc;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.Player;
import no.erlyberly.bootlegterraria.helpers.RoundTo;

public class ConsoleHandler {

    private Console console;
    private GameMain game;


    public ConsoleHandler(GameMain game) {
        this.game = game;
        console =
            new GUIConsole(new Skin(Gdx.files.internal("skins/l33t_skin/uiskin.json")), true, Input.Keys.APOSTROPHE);

        console.log("INITIATING HACKING CONSOLE v0.96", LogLevel.ERROR);
        console.log("BOOTING SEQUENCE FINISHED IN " + RoundTo.RoundDownToNearest((float) (Math.random() * 4f), 0.001f),
                    LogLevel.ERROR);

        console.setCommandExecutor(executor);
    }

    CommandExecutor executor = new CommandExecutor() {

        public void god() {
            Player player = game.getGameMap().getPlayer();
            player.god = !player.god;
            heal((byte) 4);
        }

        @ConsoleDoc(description = "Heal player in different ways",
                    paramDescriptions = {"\n0b01 - health\n0b10 - stamina"})
        public void heal(byte type) {
            Player player = game.getGameMap().getPlayer();

            if ((type & 1) != 0) { player.setHp(player.getMaxHp()); }
            if ((type & 2) != 0) { player.setStamina(player.getMaxStamina()); }
        }

        public void vSync(boolean vSync) {
            Gdx.graphics.setVSync(vSync);
        }

        @ConsoleDoc(description = "Load a map", paramDescriptions = "The map to load")
        public void load(String mapName) {
            if (!mapName.endsWith(".tmx")) {
                mapName += ".tmx";
            }
            game.loadMap(mapName);
        }
    };

    public void draw() {
        console.draw();
    }

    public boolean isConsoleVisible() {
        return console.isVisible();
    }

    public void log(String msg) {
        console.log(msg);
    }

    public void log(String msg, Object... objs) {
        console.log(String.format(msg, objs));
    }

    public void log(String msg, LogLevel level) {
        console.log(msg, level);
    }

    public void log(String msg, LogLevel level, Object... objs) {
        console.log(String.format(msg, objs), level);
    }
}
