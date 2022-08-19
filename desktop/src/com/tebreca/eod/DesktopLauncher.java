package com.tebreca.eod;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.tebreca.eod.helper.config.Settings;

import static com.tebreca.eod.App.injector;


public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		Settings settings = injector.getInstance(Settings.class);
		config.setForegroundFPS(60);
		config.setTitle("Neo-Coliseum");
		config.setResizable(false);
		if(settings.isFullscreen()){
			config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		} else {
			config.setWindowedMode(settings.getWidth(), settings.getHeight());
		}
		new Lwjgl3Application(App.getInstance(), config);
	}
}
