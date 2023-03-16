package com.apet2929.clothsim;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.apet2929.clothsim.Simulator;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(Simulator.WIDTH,Simulator.HEIGHT);
		config.setForegroundFPS(120);
		config.setTitle("ClothSimulator");
		new Lwjgl3Application(new OilSlick(), config);
	}
}
