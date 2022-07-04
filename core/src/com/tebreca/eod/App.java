package com.tebreca.eod;

import com.badlogic.gdx.ApplicationAdapter;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tebreca.eod.client.NeoClient;
import com.tebreca.eod.helper.RegistryHandler;
import com.tebreca.eod.inject.FontModule;
import com.tebreca.eod.inject.GameModule;
import com.tebreca.eod.server.NeoServer;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.IGameState;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class App extends ApplicationAdapter {

    public static final Injector injector = Guice.createInjector(new GameModule(), new FontModule());
    public static final int DEFAULT_PORT = 12300;
    private static App app = new App();
    NeoServer server;
    NeoClient client;
    GameStateManager stateManager;
    private Thread clientThread;

    public void stopServer() {
        server.shutdown();
        serverThread = null;
    }

    public Thread getServerThread() {
        return serverThread;
    }

    private Thread serverThread;

    public static App getInstance() {
        return app;
    }

    @Override
    public void create() {
        Logger root = Logger.getRootLogger();
        root.addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
        root.setLevel(Level.INFO);
        RegistryHandler.executeRegistries();
        this.stateManager = injector.getInstance(GameStateManager.class);
        this.stateManager.setCurrentState(injector.getInstance(IGameState.class));
        this.client = injector.getInstance(NeoClient.class);
        this.client.start();
    }

    @Override
    public void render() {
        stateManager.getCurrentState().render();
    }

    @Override
    public void dispose() {
        stateManager.getCurrentState().disable();
        if (serverThread != null) stopServer();
        if (clientThread != null) client.shutdown();
    }

    @Override
    public void resize(int width, int height) {
        stateManager.getCurrentState().resize(width, height);
    }

    public void setServerThread(Thread thread) {
        serverThread = thread;
    }

    public void startServer() {
        NeoServer server = injector.getInstance(NeoServer.class);
        Thread thread = new Thread(server);
        thread.setName("Server-Thread");
        thread.start();
        this.server = server;
        this.setServerThread(thread);
    }

    public void connect(){
        if(client == null) {
            client = injector.getInstance(NeoClient.class);
        }
        client.connect("localhost", DEFAULT_PORT);
    }

    public void connect(String ip){
        if(client == null) {
            client = injector.getInstance(NeoClient.class);
        }
        client.connect(ip, DEFAULT_PORT);
    }

    public void connect(String ip, int port){
        if(client == null) {
            client = injector.getInstance(NeoClient.class);
        }
        client.connect(ip, port);
    }
}
