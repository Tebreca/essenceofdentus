package com.tebreca.eod.inject;

import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
import com.google.inject.*;
import com.tebreca.eod.common.logic.Hitbox;
import com.tebreca.eod.common.logic.Position;
import com.tebreca.eod.common.map.Map;
import com.tebreca.eod.helper.config.Settings;
import com.tebreca.eod.packet.RuleRegistry;
import com.tebreca.eod.server.GameManager;
import com.tebreca.eod.server.NeoServer;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.IGameState;
import com.tebreca.eod.states.impl.MainMenuState;
import org.apache.log4j.Logger;
import org.checkerframework.checker.index.qual.SearchIndexFor;

import java.io.*;

public class GameModule extends AbstractModule {

    private final Gson gson = new Gson();
    private final Logger logger = Logger.getLogger(GameModule.class);

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


    @Singleton
    @Provides
    public Map map(){
        Map obj;
        File map = new File("./map/config.json");
        if (!map.exists()){
            obj = new Map(new Position[0], new Position[0],  new Hitbox[0], "./background.png");
            try {
                if (!map.getParentFile().mkdirs() || !map.createNewFile())
                    throw new IOException("File " + map.getAbsolutePath() +  " could not be created");
                FileWriter fileWriter = new FileWriter(map);
                gson.toJson(obj, fileWriter);
                fileWriter.close();
            } catch (IOException e) {
                logger.error("Error while trying to write map!", e);
            }
        } else {
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(map);
                obj = gson.fromJson(fileReader, Map.class);
                fileReader.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return obj != null ? obj : new Map(new Position[0], new Position[0],  new Hitbox[0], "./background.png");
    }
}
