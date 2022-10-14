package com.tebreca.eod.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.google.inject.Injector;
import com.tebreca.eod.App;
import com.tebreca.eod.common.Player;
import com.tebreca.eod.packet.PacketRule;
import com.tebreca.eod.packet.RuleRegistry;
import com.tebreca.eod.packet.rules.*;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

import java.awt.event.ActionEvent;
import java.util.*;


public class NeoServer implements Runnable, Listener {

    private final GameManager gameManager;
    Server socket = new Server();
    private final Injector injector;
    public static final Logger logger = Logger.getLogger(NeoServer.class);
    ServerUI ui = new ServerUI();
    private boolean enabled = false;
    boolean inGame = false;
    Timer scheduler = new Timer();

    public static int lobbySize = 8;

    Map<Connection, Player> clients = new HashMap<>(6);

    List<String> bannedUUIDs = new ArrayList<>();

    public AppenderSkeleton appender;

    public NeoServer(Injector injector, GameManager gameManager) {
        this.injector = injector;
        this.gameManager = gameManager;
    }

    public synchronized boolean start() throws Exception {
        enabled = true;
        logger.setLevel(Level.INFO);
        appender = new AppenderSkeleton() {
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
        };
        logger.addAppender(appender);
        gameManager.setupAppender(appender);
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
                return;
            }
            if (clients.size() == lobbySize && !clients.containsKey(connection)) {
                connection.sendTCP(JoinRule.Response.full());
                return;
            }
            if (isInGame()) {
                connection.sendTCP(JoinRule.Response.ingame());
                return;
            }
            connection.sendTCP(JoinRule.Response.ok());
            Player player = clients.get(connection);
            player.setUsername(rule.getUsername());
            player.setUuid(rule.getUserID());
            sendTCPToAll(new PlayerListRule(clients.values().toArray(Player[]::new)));
            ui.addPlayer(player);
            return;
        }
        if (o instanceof JoinTeamRule rule) {
            if (gameManager.hasplayer(rule.userId()))
                return;
            if (gameManager.canfitPlayer(rule.teamId())) {
                gameManager.addToTeam(clients.values().stream().filter(p -> p.uuid().equals(rule.userId()))
                        .findAny().orElseThrow(() -> new IllegalArgumentException("Rule specified unknown player!")), rule.teamId());
                sendTCPToAll(rule.ok());
                logger.info("Teams just got updated!");
                logger.info(gameManager.orangeTeam);
                logger.info(gameManager.purpleTeam);
                return;
            }
            connection.sendTCP(rule.negative());
            return;
        }
        if (o instanceof SelectChampRule rule) {
            boolean flag = gameManager.tryAssignCharacter(rule);
            if (flag) {
                sendTCPToAll(rule.ok());
            } else {
                connection.sendTCP(rule.negative());
            }
        } if (o instanceof LoadedIntoGameRule rule){
            Player player = clients.get(connection);
            logger.info("Player " + player.username() + " succesfully loaded in!");
            if(gameManager.markLoaded(player)){
                logger.info("All players loaded in succesfully! Game starting!");
                sendTCPToAll(new LoadedIntoGameRule.Response());
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
            if (inGame) {
                gameManager.handleLeave(clients.get(connection));
            }
            clients.remove(connection);
            sendTCPToAll(new PlayerListRule(clients.values().toArray(Player[]::new)));
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

    public void startLobby(ActionEvent event, boolean quickstart) {
        if (inGame) {
            return;
        }
        gameManager.setPlayerList(new ArrayList<>(clients.values()));
        inGame = true;
        TimerTask startgame = new TimerTask() {
            @Override
            public void run() {
                gameManager.startGame();
                sendTCPToAll(new StartGameRule());
            }
        };
        TimerTask champselect = new TimerTask() {
            @Override
            public void run() {
                gameManager.initTeams();
                scheduler.schedule(startgame, quickstart ? 3000 : 20000);
                sendTCPToAll(new StartChampSelectRule(startgame.scheduledExecutionTime(), gameManager.getIds(1), gameManager.getIds(2)));
            }
        };

        scheduler.schedule(champselect, quickstart ? 2000 : 10000);
        long startTime = champselect.scheduledExecutionTime();
        sendTCPToAll(new StartPreGameRule(startTime));
    }

    public void startLobby(ActionEvent event) {
        this.startLobby(event, false);
    }

    public void sendTCPToAll(Object packet) {
        clients.forEach((c, p) -> c.sendTCP(packet));
    }
}
