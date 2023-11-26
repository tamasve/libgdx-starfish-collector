package com.starfishcoll.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
//import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.starfishcoll.StarfishGame;


/**
 * The last version of Starfish Collector Game with multiple screens
 */

public class DesktopLauncher {

	public static void main (String[] arg) {
		//LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new StarfishGame(), "Starfish Collector", 800, 600);

	}
}