package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.LogLevel;
import com.strongjoshua.console.annotation.ConsoleDoc;
import com.strongjoshua.console.annotation.HiddenCommand;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.entities.Player;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.entities.weapons.weapons.Gun;
import no.erlyberly.bootlegterraria.entities.weapons.weapons.Sword;
import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import static no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer.logLightTime;

/**
 * @author kheba
 */
@SuppressWarnings("unused")
public class CommandHandler extends CommandExecutor {

    private final ConsoleHandler cmdH;

    CommandHandler(ConsoleHandler consoleHandler) {
        this.cmdH = consoleHandler;
    }

    public void god() {
        final Player player = GameMain.inst().getGameMap().getPlayer();
        player.god = !player.god;
        heal((byte) 3);
        this.console.log("Godmode is " + (player.god ? "enabled" : "disabled"), LogLevel.SUCCESS);
    }

    @ConsoleDoc(description = "Heal player in different ways", paramDescriptions = {"\n0b01 - health\n0b10 - stamina"})
    public void heal(final byte type) {
        final Player player = GameMain.inst().getGameMap().getPlayer();

        if ((type & 1) != 0) { player.modifyHp(player.getMaxHealth()); }
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
        GameMain.inst().loadMap(mapName);
    }

    public void gravity(final float newGravity) {
        GameMap.gravity = newGravity;
        this.console.log("New gravity is " + newGravity, LogLevel.SUCCESS);
    }

    public void wpn(final String weaponType) {
        final Player player = GameMain.inst().getGameMap().getPlayer();

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

    @ConsoleDoc(description = "Set the block at a location.",
                paramDescriptions = {"X coord of block", "Y coord of block", "TileID of new block"})
    public void setBlock(final int x, final int y, final int tileID) {
        final TileType tt = TileType.getTileTypeById(tileID);
        GameMain.inst().getGameMap().setBlockAt(x, y, tt);
    }

    @HiddenCommand
    public void logLight() {
        logLightTime = !logLightTime;
        this.console.log("Logging light is now " + logLightTime);
    }

    @ConsoleDoc(description = "Teleport the player to a valid location", paramDescriptions = {"X value", "Y value"})
    public void tp(int x, int y) {
        if (!Util.isBetween(0, x, (int) GameMain.inst().getGameMap().getWidth())) {
            this.cmdH.logf("Cannot teleport player outside of map", LogLevel.ERROR);
            return;
        }
        GameMap gameMap = GameMain.inst().getGameMap();
        this.cmdH.logf("Trying to teleport player to (%d, %d)", x, y);
        Vector2 v = gameMap.getPlayer().teleport(x, y);

        if (!v.equals(new Vector2(x, y))) {
            this.cmdH.log("Failed to teleport player to original coordinate.");
            this.cmdH.logf("Player teleported to (%.0f, %.0f)", LogLevel.SUCCESS, v.x, v.y);
        }
        else {
            this.cmdH.log("Successfully teleported player", LogLevel.SUCCESS);
        }
    }

    public void fly() {
        Player p = GameMain.inst().getGameMap().getPlayer();
        p.setFlying(!p.isFlying());
        GameMain.consHldr().logf("Flight status: %b", LogLevel.SUCCESS, p.isFlying());
    }

    public void speed(float speed) {
        GameMain.inst().getGameMap().getPlayer().flightSpeed = speed;
        GameMain.consHldr().logf("Flying speed: %.2f", LogLevel.SUCCESS, speed);
    }
}
