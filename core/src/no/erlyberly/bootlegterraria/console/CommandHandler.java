package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Gdx;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.LogLevel;
import com.strongjoshua.console.annotation.ConsoleDoc;
import com.strongjoshua.console.annotation.HiddenCommand;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.Player;
import no.erlyberly.bootlegterraria.entities.weapons.Gun;
import no.erlyberly.bootlegterraria.entities.weapons.Sword;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import static no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer.logLightTime;

/**
 * @author kheba
 */
public class CommandHandler extends CommandExecutor {

    private final GameMain game;

    CommandHandler(final GameMain game) {

        this.game = game;
    }

    public void god() {
        final Player player = this.game.getGameMap().getPlayer();
        player.god = !player.god;
        heal((byte) 3);
        this.console.log("Godmode is " + (player.god ? "enabled" : "disabled"), LogLevel.SUCCESS);
    }

    @ConsoleDoc(description = "Heal player in different ways", paramDescriptions = {"\n0b01 - health\n0b10 - stamina"})
    public void heal(final byte type) {
        final Player player = this.game.getGameMap().getPlayer();

        if ((type & 1) != 0) { player.setHp(player.getMaxHp()); }
        if ((type & 2) != 0) { player.setStamina(player.getMaxStamina()); }
    }

    public void vSync(final boolean vSync) {
        Gdx.graphics.setVSync(vSync);
    }

    @ConsoleDoc(description = "Load a map", paramDescriptions = "The map to load")
    public void load(String mapName) {
        if (!mapName.endsWith(".tmx")) {
            mapName += ".tmx";
        }
        this.game.loadMap(mapName);
    }

    public void gravity(final float newGravity) {
        GameMap.gravity = newGravity;
        this.console.log("New gravity is " + newGravity, LogLevel.SUCCESS);
    }

    public void wpn(final String weaponType) {
        final Player player = this.game.getGameMap().getPlayer();

        final Weapon weapon;

        switch (weaponType.toLowerCase()) {
            case "gun":
                weapon = new Gun("Elge bøsji");
                break;
            case "sword":
                weapon = new Sword("The Roll");
                break;
            default:
                this.console.log("Could not find weaponType '" + weaponType + "'", LogLevel.ERROR);
                return;
        }

        player.setWeapon(weapon);
    }

    public void setBlock(final int x, final int y, final int tileID) {

        final TileType tt = TileType.getTileTypeById(tileID);
        System.out.println("tt = " + tt);
        this.game.getGameMap().setBlockAt(x, y, tt);
    }

    @HiddenCommand
    public void logLight() {
        logLightTime = !logLightTime;
        this.console.log("Logging light is now " + logLightTime);
    }
}
