package com.tebreca.eod.inject;

import com.google.gson.Gson;
import com.google.inject.*;
import com.tebreca.eod.helper.config.Settings;
import com.tebreca.eod.packet.RuleRegistry;
import com.tebreca.eod.server.GameManager;
import com.tebreca.eod.server.NeoServer;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.IGameState;
import com.tebreca.eod.states.impl.MainMenuState;

import java.io.*;

public class GameModule extends AbstractModule {

    private final Gson gson = new Gson();

    @Singleton
    @Provides
    public Gson getGson() {
        return gson;
    }

    @Singleton
    @Provides
    public GameStateManager getGameStateManager() {
        return new GameStateManager();
    }

    @Override
    protected void configure() {
        //default gamestate to fall back to
        bind(IGameState.class).to(MainMenuState.class);
        super.configure();
    }

    @Singleton
    @Provides
    public RuleRegistry registry() {
        return RuleRegistry.instance;
    }

    @Singleton
    @Provides
    public Settings settings() {
        File file = new File("./settings.json");
        if (!file.exists()) {
            return new Settings();
        }
        try {
            FileReader stream = new FileReader(file);
            Settings settings = gson.fromJson(stream, Settings.class);
            stream.close();
            return settings;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Settings();
    }

    private boolean newServer = true;
    private NeoServer server;

    public void setNewServer(boolean newServer) {
        this.newServer = newServer;
    }

    @Provides
    public NeoServer server(GameManager manager, Injector injector) {
        if (newServer) {
            newServer =false;
            server = new NeoServer(injector, manager);
        }
        return server;
    }

}
