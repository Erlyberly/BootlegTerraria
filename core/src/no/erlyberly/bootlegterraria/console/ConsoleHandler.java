package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.Console;
import com.strongjoshua.console.GUIConsole;
import no.erlyberly.bootlegterraria.entities.Player;
import no.erlyberly.bootlegterraria.world.GameMap;

public class ConsoleHandler {

    private Console console;
    private GameMap game;


    public ConsoleHandler(GameMap game) {
        this.game = game;
        console = new GUIConsole(new Skin(Gdx.files.classpath("default_skin/uiskin.json")), true, Input.Keys.DEL);
        console.setCommandExecutor(executor);
    }

    CommandExecutor executor = new CommandExecutor() {

        public void GOD() {
            Player player = game.getPlayer();
            boolean in = !player.isInvincible();
            player.setInvincible(in);
            console.log("You are now" + (in ? "" : " not") + " invincible");
        }

    };


    public void draw() {
        console.draw();
    }
}
