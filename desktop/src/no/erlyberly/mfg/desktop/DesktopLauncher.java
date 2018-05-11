package no.erlyberly.mfg.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import helpers.GameInfo;
import no.erlyberly.mfg.GameMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width = GameInfo.WIDTH;
		config.height = GameInfo.HEIGHT;

		new LwjglApplication(new GameMain(), config);
	}
}
