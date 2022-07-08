package com.tebreca.eod.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tebreca.eod.App;
import com.tebreca.eod.common.Player;
import com.tebreca.eod.packet.PacketRule;
import com.tebreca.eod.packet.RuleRegistry;
import com.tebreca.eod.packet.rules.JoinRule;
import com.tebreca.eod.packet.rules.PlayerListRule;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.event.ActionEvent;
import java.util.*;

@Singleton
public class NeoServer implements Runnable, Listener {

    Server socket = new Server();
    private final Injector injector;
    private static final Logger logger = Logger.getLogger(NeoServer.class);
    ServerUI ui = new ServerUI();
    private boolean enabled = false;
    boolean inGame = false;
    Timer scheduler = new Timer();

    public static int lobbySize = 8;

    Map<Connection, Player> clients = new HashMap<>(6);

    List<String> bannedUUIDs = new ArrayList<>();

    @Inject
    public NeoServer(Injector injector) {
        this.injector = injector;
    }

    public boolean start() throws Exception {
        enabled = true;
        logger.setLevel(Level.INFO);
        logger.addAppender(new AppenderSkeleton() {
            Layout layout = new PatternLayout("%r [%t] %p %c %x - %m%n");

            @Override
            protected void append(LoggingEvent event) {
                ui.append(layout.format(event));
            }

            @Override
            public void close() {
                layout = null;
            }

            @Override
            public Layout getLayout() {
                return layout;
            }

            @Override
            public boolean requiresLayout() {
                return true;
            }
        });
        socket.start();
        Kryo serverKryo = socket.getKryo();
        PacketRule[] rules = injector.getInstance(RuleRegistry.class).getAllUnordered();
        for (PacketRule rule : rules) {
            rule.register(serverKryo);
        }
        socket.addListener(this);
        socket.bind(App.TCP_PORT, App.UDP_PORT);
        ui.subscribeToClick(this::startLobby);
        ui.show();

        return true;
    }

    private boolean isInGame() {
        return inGame;
    }

    public int getLobbySize() {
        return lobbySize;
    }

    public static void setLobbySize(int lobbySize) {
        NeoServer.lobbySize = lobbySize;
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
        if (o instanceof JoinRule rule) {
            if (bannedUUIDs.contains(rule.getUserID())) {
                connection.sendTCP(JoinRule.Response.banned());
            } else if (clients.size() == lobbySize && !clients.containsKey(connection)) {
                connection.sendTCP(JoinRule.Response.full());
            } else if (isInGame()) {
                connection.sendTCP(JoinRule.Response.ingame());
            } else {
                connection.sendTCP(JoinRule.Response.ok());
                Player player = clients.get(connection);
                player.setUsername(rule.getUsername());
                player.setUuid(rule.getUserID());
                clients.keySet().forEach(c -> c.sendTCP(new PlayerListRule(clients.values().toArray(Player[]::new))));
                ui.addPlayer(player);
            }
        }
    }

    @Override
    public void connected(Connection connection) {
        if (clients.size() >= lobbySize) {
            logger.warn("Server is full! Rejecting connection from: " + connection.getRemoteAddressUDP());
            return;
        }
        clients.put(connection, new Player(null, null));
        logger.info("New incoming connection from: " + connection.getRemoteAddressUDP());
    }


    @Override
    public void disconnected(Connection connection) {
        if (!clients.containsKey(connection)) {
            return;
        }
        ui.removePlayer(clients.get(connection));
        logger.info("Client disconnected: " + connection.getRemoteAddressUDP());
        if (enabled) {
            clients.remove(connection);
            clients.keySet().forEach(c -> c.sendTCP(new PlayerListRule(clients.values().toArray(Player[]::new))));
        }
    }

    public void shutdown() {
        enabled = false;
        try {
            clients.keySet().forEach(Connection::close);
        } catch (ConcurrentModificationException e) {
            logger.error("Client closed before server!", e);
        }
        socket.close();
    }

    private void startLobby(ActionEvent event) {
        inGame = true;


    }
}
