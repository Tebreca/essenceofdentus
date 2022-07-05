package com.tebreca.eod.server;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tebreca.eod.App;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

@Singleton
public class NeoServer extends Listener implements Runnable {

    Server server = new Server();
    private final Injector injector;
    private static final Logger logger = Logger.getLogger(NeoServer.class);
    @Inject
    public NeoServer(Injector injector) {
        this.injector = injector;
    }


    public boolean start() throws Exception {
        logger.setLevel(Level.INFO);
        server.start();
        server.addListener(this);
        server.bind(App.DEFAULT_PORT);
        return true;
    }


    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            logger.error("Exception thrown in server networking thread:", e);
        }
    }


    @Override
    public void received(Connection connection, Object o) {
        super.received(connection, o);

    }

    public void shutdown() {
        server.close();
    }
}
