package no.erlyberly.bootlegterraria.console;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
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
import no.erlyberly.bootlegterraria.storage.TileStack;
import no.erlyberly.bootlegterraria.storage.impl.AutoSortedContainer;
import no.erlyberly.bootlegterraria.storage.impl.Container;
import no.erlyberly.bootlegterraria.storage.impl.CreativeInventory;
import no.erlyberly.bootlegterraria.storage.impl.Inventory;
import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.world.GameMap;
import no.erlyberly.bootlegterraria.world.TileType;

import java.util.Arrays;
import java.util.Objects;

import static no.erlyberly.bootlegterraria.render.SimpleOrthogonalTiledMapRenderer.logLightEvents;

/**
 * @author kheba
 */
@SuppressWarnings("unused")
public class CommandHandler extends CommandExecutor {

    private final ConsoleHandler cmdH;

    CommandHandler(final ConsoleHandler consoleHandler) {
        cmdH = consoleHandler;
    }

    public void god() {
        final Player player = GameMain.map.getPlayer();
        player.god = !player.god;
        heal((byte) 3);
        console.log("Godmode is " + (player.god ? "enabled" : "disabled"), LogLevel.SUCCESS);
    }

    @ConsoleDoc(description = "Heal player in different ways", paramDescriptions = {"\n0b01 - health\n0b10 - stamina"})
    public void heal(final byte type) {
        final Player player = GameMain.map.getPlayer();

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
        GameMain.loadMap(mapName);
    }

    @ConsoleDoc(paramDescriptions = "Change gravity")
    public void gravity(final float newGravity) {
        GameMap.gravity = newGravity;
        console.log("New gravity is " + newGravity, LogLevel.SUCCESS);
    }

    @ConsoleDoc(description = "Switch weapon", paramDescriptions = {"Available weapon types are 'gun' and 'sword'"})
    public void wpn(final String weaponType) {
        final Player player = GameMain.map.getPlayer();

        final Weapon weapon;

        switch (weaponType.toLowerCase()) {
            case "gun":
                weapon = new Gun("Elge bøsji");
                break;
            case "sword":
                weapon = new Sword("The Roll");
                break;
            default:
                console.log("Could not find weaponType '" + weaponType + "'", LogLevel.ERROR);
                return;
        }

        player.setWeapon(weapon);
    }

    @ConsoleDoc(description = "Set the block at a location.",
                paramDescriptions = {"X coord of block", "Y coord of block", "TileID of new block"})
    public void setBlock(final int x, final int y, final int tileID) {
        final TileType tt = TileType.getTileTypeById(tileID);
        GameMain.map.setTile(x, y, tt);
    }

    @HiddenCommand
    public void logLight() {
        logLightEvents = !logLightEvents;
        console.log("Logging light is now " + logLightEvents);
    }

    @ConsoleDoc(description = "Teleport the player to a valid location", paramDescriptions = {"X value", "Y value"})
    public void tp(final int x, final int y) {
        if (!Util.isBetween(0, x, (int) GameMain.map.getWidth())) {
            cmdH.log("Cannot teleport player outside of map", LogLevel.ERROR);
            return;
        }
        final GameMap gameMap = GameMain.map;
        cmdH.logf("Trying to teleport player to (%d, %d)", x, y);
        final Vector2 v = gameMap.getPlayer().teleport(x, y);

        if (!v.equals(new Vector2(x, y))) {
            cmdH.log("Failed to teleport player to original coordinate.");
            cmdH.logf(LogLevel.SUCCESS, "Player teleported to (%.0f, %.0f)", v.x, v.y);
        }
        else {
            cmdH.log("Successfully teleported player", LogLevel.SUCCESS);
        }
    }

    @ConsoleDoc(description = "Toggle flight")
    public void fly() {
        final Player p = GameMain.map.getPlayer();
        p.setFlying(!p.isFlying());
        p.speedModifier += p.isFlying() ? 1 : -1;
        GameMain.console.logf(LogLevel.SUCCESS, "Flight status: %b", p.isFlying());
    }

    @ConsoleDoc(description = "Set the speed modifier of the player")
    public void speed(final float speed) {
        GameMain.map.getPlayer().speedModifier = speed;
        speed();
    }

    @ConsoleDoc(description = "Display the current speed modifier of the player")
    public void speed() {
        GameMain.console.logf(LogLevel.SUCCESS, "Flying speedModifier: %.2f", GameMain.map.getPlayer().speedModifier);
    }

    @ConsoleDoc(description = "Show how the light really is ")
    public void debugLight(final boolean debug) {
        BlockLightMap.realLight = !debug;
        SimpleOrthogonalTiledMapRenderer.logLightEvents = debug;
        GameMain.console.log("Light debugging: " + debug, LogLevel.SUCCESS);
    }

    @ConsoleDoc(description = "Switch inventory of player",
                paramDescriptions = {"The inventory to use, can be 'creative', 'autosort' or 'container'"})
    public void inv(final String invType) {
        final Player player = GameMain.map.getPlayer();
        player.getInv().close();
        TileStack[] oldContent = null;
        if (player.getInv().getContainer() != null) {
            oldContent = Arrays.stream(player.getInv().getContainer().getContent()).filter(Objects::nonNull)
                               .toArray(TileStack[]::new);
        }
        switch (invType.toLowerCase()) {
            case "creative":
            case "cr":
                player.setInv(new CreativeInventory(player));
                break;
            case "as":
            case "autosort":
                player.setInv(new Inventory(player, new AutoSortedContainer("Auto Sorted Inventory", 40)));
                break;
            case "container":
            case "co":
                player.setInv(new Inventory(player, new Container("Inventory", 40, true)));
                break;
            default:
                GameMain.console.log("Unknown storage type '" + invType + '\'', LogLevel.ERROR);
                return;
        }
        if (oldContent != null && player.getInv().getContainer() != null) {
            player.getInv().getContainer().add(oldContent);
        }
        GameMain.console.log("New inventory type is " + player.getInv().getClass().getSimpleName(), LogLevel.SUCCESS);
    }

    @ConsoleDoc(description = "Unbind a binding, NOT WORKING ATM!! Binding needs to be rewritten with UUIDs in mind")
    @HiddenCommand
    public void unbind(final String settingStr) {
        final InputSetting setting;
        try {
            try {
                setting = InputSetting.valueOf(settingStr.toUpperCase());
            } catch (final IllegalArgumentException | NullPointerException e) {
                GameMain.console.logf(LogLevel.ERROR, "No setting found with the name '%s'", settingStr);
                return;
            }
            final InputHandler inputHandler = GameMain.input;

            final boolean success =
                inputHandler.rebindListener(setting.getEventType(), setting.getKeys(), new Integer[0]);
            if (!success) {
                return;
            }

            GameMain.console.log(setting.toString() + " removed");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @ConsoleDoc(description = "Bind three keys to a setting",
                paramDescriptions = {"The setting to bind to", "The first key you want to bind",
                    "The second key you want to bind", "The third key you want to bind"})
    public void bind(final String settingStr, final String key1, final String key2, final String key3) {
        bind0(settingStr, key1, key2, key3);
    }

    @ConsoleDoc(description = "Bind two keys to a setting",
                paramDescriptions = {"The setting to bind to", "The first key you want to bind",
                    "The second key you want to bind"})
    public void bind(final String settingStr, final String key1, final String key2) {
        bind0(settingStr, key1, key2);
    }

    @ConsoleDoc(description = "Bind a key to a setting",
                paramDescriptions = {"The setting to bind to", "The key you want to bind"})
    public void bind(final String settingStr, final String key1) {
        bind0(settingStr, key1);
    }

    //The console does not take arrays so this method must be internal
    private void bind0(final String settingStr, final String... keyStr) {
        final InputSetting setting;
        try {
            try {
                setting = InputSetting.valueOf(settingStr.toUpperCase());
            } catch (final IllegalArgumentException | NullPointerException e) {
                GameMain.console.logf(LogLevel.ERROR, "No setting found with the name '%s'", settingStr);
                return;
            }
            final Integer[] keys = new Integer[keyStr.length];
            for (int i = 0, length = keyStr.length; i < length; i++) {
                final String key = keyStr[i];

                int keyInt = Input.Keys.valueOf(Util.toTitleCase(key));
                if (keyInt == -1) {
                    keyInt = MouseInput.valueOf(key.toUpperCase());
                }
                if (keyInt == -1) {
                    GameMain.console.logf(LogLevel.ERROR, "Could not find any input with the name of '%s'", key);
                    return;
                }
                keys[i] = keyInt;
            }

            final InputHandler inputHandler = GameMain.input;

            final boolean success = inputHandler.rebindListener(setting.getEventType(), setting.getKeys(), keys);
            if (!success) {
                return;
            }

            //update keys
            setting.setKeys(keys);

            GameMain.console.log(setting.toString() + " - " + Util.keysToString(setting.getKeys()));
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @ConsoleDoc(description = "List all settings with their bindings")
    public void binds() {
        for (final InputSetting setting : InputSetting.values()) {
            GameMain.console.log(setting.toString() + " - " + Util.keysToString(setting.getKeys()));
        }
    }

    @ConsoleDoc(description = "Execute all commands in the given file, each command is on a newline",
                paramDescriptions = "The absolute path to the file to be executed")
    public void exec(final String absFile) {
        FileHandle execFile = null;

        for (final Files.FileType type : Files.FileType.values()) {
            try {
                execFile = Gdx.files.getFileHandle(absFile, type);
                if (execFile.exists() && !execFile.isDirectory()) {
                    GameMain.console.logf("Found given file '%s' using the FileType '%s'", absFile, type);
                    break;
                }
            } catch (final GdxRuntimeException ignore) { }
        }
        if (execFile != null && execFile.exists() && !execFile.isDirectory()) {
            console.log("Executing console commands from file '" + execFile.name() + "'", LogLevel.SUCCESS);
            final String[] cmds = execFile.readString().replace("\r\n", "\n").replace("\r", "\n").split("\n");

            for (final String cmd : cmds) {
                //single line comment
                int comment = cmd.indexOf("//");
                if (comment == 0) {
                    continue;
                }
                if (comment == -1) {
                    comment = cmd.length();
                }
                GameMain.console.execCommand(cmd.substring(0, comment));
            }
            console.log("Finished executing all commands from file", LogLevel.SUCCESS);
        }
        else {
            console.log("Failed to execute given file \"" + (execFile == null ? "???" : execFile.name()) +
                        "\", does it exist? is it a directory?", LogLevel.ERROR);
        }
    }

    public void openInv(final boolean open) {
        try {
            if (open) { GameMain.map.getPlayer().getInv().open(); }
            else { GameMain.map.getPlayer().getInv().close(); }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public void give(final String tileType, final int amount) {
        if (GameMain.map.getPlayer().getInv().getContainer() == null) { return; }
        final TileType tt;
        try {
            tt = TileType.valueOf(tileType.toUpperCase());
        } catch (final IllegalArgumentException e) {
            GameMain.console.logf(LogLevel.ERROR, "Unknown tileType '%s'", tileType);
            return;
        }
        GameMain.map.getPlayer().getInv().getContainer().add(tt, amount);
    }
}
