package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.strongjoshua.console.CommandExecutor;
import com.strongjoshua.console.LogLevel;
import com.strongjoshua.console.annotation.ConsoleDoc;
import com.strongjoshua.console.annotation.HiddenCommand;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.entities.living.Player;
import no.erlyberly.bootlegterraria.entities.weapons.Weapon;
import no.erlyberly.bootlegterraria.entities.weapons.weapons.Gun;
import no.erlyberly.bootlegterraria.entities.weapons.weapons.Sword;
import no.erlyberly.bootlegterraria.input.InputHandler;
import no.erlyberly.bootlegterraria.input.InputSetting;
import no.erlyberly.bootlegterraria.input.MouseInput;
import no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer;
import no.erlyberly.bootlegterraria.render.light.BlockLightMap;
import no.erlyberly.bootlegterraria.storage.impl.AutoSortedInventory;
import no.erlyberly.bootlegterraria.storage.impl.CreativeInventory;
import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import static no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer.logLightEvents;

/**
 * @author kheba
 */
@SuppressWarnings("unused")
public class CommandHandler extends CommandExecutor {

    private final ConsoleHandler cmdH;

    CommandHandler(final ConsoleHandler consoleHandler) {
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

        if (!mapName.toLowerCase().endsWith(".tmx")) {
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
        GameMain.inst().getGameMap().setTile(x, y, tt);
    }

    @HiddenCommand
    public void logLight() {
        logLightEvents = !logLightEvents;
        this.console.log("Logging light is now " + logLightEvents);
    }

    @ConsoleDoc(description = "Teleport the player to a valid location", paramDescriptions = {"X value", "Y value"})
    public void tp(final int x, final int y) {
        if (!Util.isBetween(0, x, (int) GameMain.inst().getGameMap().getWidth())) {
            this.cmdH.logf("Cannot teleport player outside of map", LogLevel.ERROR);
            return;
        }
        final GameMap gameMap = GameMain.inst().getGameMap();
        this.cmdH.logf("Trying to teleport player to (%d, %d)", x, y);
        final Vector2 v = gameMap.getPlayer().teleport(x, y);

        if (!v.equals(new Vector2(x, y))) {
            this.cmdH.log("Failed to teleport player to original coordinate.");
            this.cmdH.logf("Player teleported to (%.0f, %.0f)", LogLevel.SUCCESS, v.x, v.y);
        }
        else {
            this.cmdH.log("Successfully teleported player", LogLevel.SUCCESS);
        }
    }

    public void fly() {
        final Player p = GameMain.inst().getGameMap().getPlayer();
        p.setFlying(!p.isFlying());
        p.speedModifier += p.isFlying() ? 1 : -1;
        GameMain.consHldr().logf("Flight status: %b", LogLevel.SUCCESS, p.isFlying());
    }

    public void speed(final float speed) {
        GameMain.inst().getGameMap().getPlayer().speedModifier = speed;
        speed();
    }

    public void speed() {
        GameMain.consHldr().logf("Flying speedModifier: %.2f", LogLevel.SUCCESS,
                                 GameMain.inst().getGameMap().getPlayer().speedModifier);
    }

    public void debugLight(final boolean debug) {
        BlockLightMap.realLight = !debug;
        SimpleOrthogonalTiledMapRenderer.logLightEvents = debug;
        GameMain.consHldr().logf("Light debugging: " + debug, LogLevel.SUCCESS);
    }

    public void inv(final String invType) {
        final Player player = GameMain.inst().getGameMap().getPlayer();
        switch (invType.toLowerCase()) {
            case "creative":
            case "crea":
            case "c":
                player.setInv(new CreativeInventory(player));
                break;
            case "as":
            case "autosort":
                player.setInv(new AutoSortedInventory(16, player));
                break;
            default:
                GameMain.consHldr().log("Unknown storage type '" + invType + '\'', LogLevel.ERROR);
                return;
        }
        GameMain.consHldr().log("New storage " + player.getInv().getClass().getSimpleName(), LogLevel.SUCCESS);
    }

    public void bind(final String settingStr, final String keyStr) {
        final InputSetting setting;
        try {
            setting = InputSetting.valueOf(settingStr.toUpperCase());
        } catch (final IllegalArgumentException | NullPointerException e) {
            GameMain.consHldr().logf("No setting found with the name '%s'", LogLevel.ERROR, settingStr);
            return;
        }
        Integer key;
        key = Input.Keys.valueOf(Util.toTitleCase(keyStr));
        if (key == -1) {
            key = MouseInput.valueOf(keyStr.toUpperCase());
        }
        if (key == -1) {
            GameMain.consHldr().logf("Could not find any input with the name of '%s'", LogLevel.ERROR, keyStr);
            return;
        }

        final InputHandler inputHandler = GameMain.inst().getInputHandler();

        inputHandler.rebindListener(setting.getEventType(), setting.getKeys(), new Integer[] {key});

        //update keys
        setting.setKeys(key);

        GameMain.consHldr().log(setting.toString() + " - " + Util.keysToString(setting.getKeys()));
    }

    @ConsoleDoc(description = "List all settings with their bindings")
    public void binds() {
        for (final InputSetting setting : InputSetting.values()) {
            GameMain.consHldr().log(setting.toString() + " - " + Util.keysToString(setting.getKeys()));
        }
    }
}
