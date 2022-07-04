package com.tebreca.eod;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tebreca.eod.config.Settings;


public class DesktopLauncher {
	static Settings settings = new Settings();//	TODO: I/O
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Neo-Coliseum");
		config.setResizable(false);
		if(DesktopLauncher.settings.isFullscreen()){
			config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		} else {
			config.setWindowedMode(DesktopLauncher.settings.getWidth(), DesktopLauncher.settings.getHeight());
		}
		new Lwjgl3Application(new App(), config);
	}
}
