package com.tebreca.eod.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tebreca.eod.App;
import com.tebreca.eod.common.Player;
import com.tebreca.eod.packet.PacketRule;
import com.tebreca.eod.packet.RuleRegistry;
import com.tebreca.eod.packet.rules.JoinRule;
import com.tebreca.eod.packet.rules.PlayerListRule;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.impl.JoinState;
import com.tebreca.eod.states.impl.LobbyState;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

@Singleton
public class NeoClient implements Listener {

    private final Injector injector;
    private String username = "tebreca";
    private String userID = UUID.randomUUID().toString();
    private static final Logger logger = Logger.getLogger(NeoClient.class);

    Client client = new Client();
    Thread clientThread = new Thread(client);
    private GameStateManager stateManager;

    public Player[] getPlayers() {
        return players;
    }

    private Player[] players;

    @Inject
    public NeoClient(Injector injector) {
        this.injector = injector;
    }

    public void start() {
        stateManager = injector.getInstance(GameStateManager.class);
        clientThread.setName("Client-Thread");
        clientThread.start();
        Kryo clientKryo = client.getKryo();
        PacketRule[] rules = injector.getInstance(RuleRegistry.class).getAllUnordered();
        for (PacketRule rule : rules) {
            rule.register(clientKryo);
        }
        client.addListener(this);
    }


    public void connect(InetAddress address) {
        try {
            client.connect(5000, address, App.TCP_PORT, App.UDP_PORT);
        } catch (IOException e) {
            logger.error("Exception thrown when trying to connect to Server; ", e);
            if (stateManager.getCurrentState() instanceof JoinState state) {
                state.error(e.getMessage());
            }
        }
        client.sendTCP(new JoinRule(userID, username));
    }

    public void connect(String ip, int tcpPort, int udpPort) {
        try {
            client.connect(5000, ip, tcpPort, udpPort);
        } catch (IOException e) {
            logger.error("Exception thrown when trying to connect to Server; ", e);
            if (stateManager.getCurrentState() instanceof JoinState state){
                state.error(e.getMessage());
            }
        }
        client.sendTCP(new JoinRule(userID, username));
    }

    public void disconnect() {
        client.stop();
    }

    public void shutdown() {
        disconnect();
        client.close();
    }

    @Override
    public void received(Connection connection, Object o) {
        if (o instanceof JoinRule.Response response) {
            if (response.getStatus() != JoinRule.Response.Status.WELCOME) {
                onFailedConnect(response.getStatus());
                connection.close();
            } else {
                stateManager.getStateQueue().add(() -> injector.getInstance(LobbyState.class));
                //TODO
            }
        } else if(o instanceof PlayerListRule rule){
            players = rule.players();

        }
    }

    public InetAddress scanLAN() {
        return client.discoverHost(App.TCP_PORT, App.UDP_PORT);
    }

    public void onFailedConnect(JoinRule.Response.Status status){
        logger.warn(status.getError());
        if (stateManager.getCurrentState() instanceof JoinState state){
            state.error(status.getError());
        } else {
            stateManager.getStateQueue().add(()->supplier(status));
        }
    }

    private JoinState supplier(JoinRule.Response.Status status){
        JoinState state = injector.getInstance(JoinState.class);
        state.error(status.getError());
        return state;
    }

    public void setUsername(String text) {
        this.username = text;
    }
}
