package no.erlyberly.bootlegterraria.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import no.erlyberly.bootlegterraria.GameMain;
import no.erlyberly.bootlegterraria.util.GameInfo;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

        config.width = GameInfo.WIDTH;
        config.height = GameInfo.HEIGHT;
        config.vSyncEnabled = true;
        config.foregroundFPS = 9999;
        config.backgroundFPS = 10;
        config.samples = 16;
        config.resizable = false; //it's not you, it's the lines
        config.title = "Bootleg Terraria";
        config.addIcon("ErlyBerly_TheGreat_icon.png", Files.FileType.Internal);
        new LwjglApplication(new GameMain(), config);
    }
}
