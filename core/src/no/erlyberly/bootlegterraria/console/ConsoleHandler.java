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
        console = new GUIConsole(new Skin(Gdx.files.internal("skins/l33t_skin/uiskin.json")), true, Input.Keys.DEL);
        console.setCommandExecutor(executor);
    }

    CommandExecutor executor = new CommandExecutor() {

        public void god() {
            Player player = game.getPlayer();
            player.god = !player.god;
            heal();
        }

        public void heal() {
            Player player = game.getPlayer();
            player.setHp(player.getMaxHp());
            player.setStamina(player.getMaxStamina());
        }
    };


    public void draw() {
        console.draw();
    }
}
