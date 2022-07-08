package com.tebreca.eod;

import com.badlogic.gdx.ApplicationAdapter;
import com.esotericsoftware.minlog.Log;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.tebreca.eod.client.NeoClient;
import com.tebreca.eod.inject.FontModule;
import com.tebreca.eod.inject.GameModule;
import com.tebreca.eod.server.NeoServer;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.IGameState;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.esotericsoftware.minlog.Log.*;

public class App extends ApplicationAdapter {

    public static final Injector injector = Guice.createInjector(new GameModule(), new FontModule());
    public static final int TCP_PORT = 12300;
    public static final int UDP_PORT = 12301;
    private static final App app = new App();
    private final Logger logger = Logger.getLogger(App.class);
    NeoServer server;
    NeoClient client;
    GameStateManager stateManager;
    private Thread clientThread;

    public void setUsername(String text) {
        client.setUsername(text);
    }

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
        Log.set(LEVEL_ERROR);
        Logger root = Logger.getRootLogger();
        root.addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
        root.setLevel(Level.INFO);
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
        if (serverEnabled()) stopServer();
        if (clientThread != null) client.shutdown();
        System.exit(0);
    }

    @Override
    public void resize(int width, int height) {
        stateManager.getCurrentState().resize(width, height);
    }

    public void setServerThread(Thread thread) {
        serverThread = thread;
    }

    public void startServer() {
        this.server = injector.getInstance(NeoServer.class);
        this.serverThread = new Thread(server);
        serverThread.setName("Server-Thread");
        serverThread.start();
    }

    public void connect() {
        if (client == null) {
            client = injector.getInstance(NeoClient.class);
        }
        try {
            client.connect(InetAddress.getByName("localhost"));
        } catch (UnknownHostException e) {
            logger.error("Couldnt find localhost! ", e);
        }
    }

    public void connect(String ip) {
        if (client == null) {
            client = injector.getInstance(NeoClient.class);
        }
        client.connect(ip, TCP_PORT, UDP_PORT);
    }

    public void connect(String ip, int tcpPort, int udpPort) {
        if (client == null) {
            client = injector.getInstance(NeoClient.class);
        }
        client.connect(ip, tcpPort, udpPort);
    }

    public boolean serverEnabled() {
        return serverThread != null;
    }
}
