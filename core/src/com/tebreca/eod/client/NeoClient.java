package com.tebreca.eod.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tebreca.eod.App;
import com.tebreca.eod.client.texture.CharacterTextureData;
import com.tebreca.eod.client.texture.TextureSheet;
import com.tebreca.eod.common.Player;
import com.tebreca.eod.common.character.Character;
import com.tebreca.eod.common.character.CharacterRegistry;
import com.tebreca.eod.packet.PacketRule;
import com.tebreca.eod.packet.RuleRegistry;
import com.tebreca.eod.packet.rules.*;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.impl.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

@Singleton
public class NeoClient implements Listener {

    private final Injector injector;
    public String username = "tebreca";
    public String userID = UUID.randomUUID().toString();
    private static final Logger logger = Logger.getLogger(NeoClient.class);
    public Map<Character, TextureSheet> characterTextureSheets = new HashMap<>();

    private Connection connection;
    Client client = new Client();
    Thread clientThread = new Thread(client);
    private GameStateManager stateManager;
    private boolean exiting = false;

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

        CharacterRegistry characterRegistry = injector.getInstance(CharacterRegistry.class);
        for (Character character : characterRegistry.getAllUnordered()) {
            Optional<CharacterTextureData> data = TextureSheetReader.getForCharacter(character);
            if (data.isEmpty()) {
                continue;
            }
            TextureSheet sheet = TextureSheetReader.from(data.get());
            characterTextureSheets.put(character, sheet);
        }
    }


    public synchronized void connect(InetAddress address) {
        exiting = false;
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

    public synchronized void connect(String ip, int tcpPort, int udpPort) {
        exiting = false;
        try {
            client.connect(5000, ip, tcpPort, udpPort);
        } catch (IOException e) {
            logger.error("Exception thrown when trying to connect to Server; ", e);
            if (stateManager.getCurrentState() instanceof JoinState state) {
                state.error(e.getMessage());
            }
        }
        client.sendTCP(new JoinRule(userID, username));
    }

    public void disconnect() {
        exiting = true;
        connection.close();
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
                return;
            }
            stateManager.getStateQueue().add(() -> injector.getInstance(LobbyState.class));
            return;
        }
        if (o instanceof PlayerListRule rule) {
            players = rule.players();
            return;
        }
        if (o instanceof JoinTeamRule.Response response) {
            if (response.ok()) {
                if (stateManager.getCurrentState() instanceof PreGameState state) {
                    state.playerJoins(response.rule().teamId(), getPlayer(response.rule().userId()));
                } else {
                    stateManager.getStateQueue().add(() -> preGameStateSupplier(response));
                }
            }
            return;
        }
        if (o instanceof StartPreGameRule rule) {
            stateManager.getStateQueue().add(() -> {
                PreGameState instance = injector.getInstance(PreGameState.class);
                instance.setTimer(rule.startTime());
                return instance;
            });
            return;
        }
        if (o instanceof StartChampSelectRule rule) {
            stateManager.getStateQueue().add(() -> {
                ChampSelectState instance = injector.getInstance(ChampSelectState.class);
                instance.setOrangeTeam(Arrays.stream(rule.orangeTeam()).map(this::getPlayer).toArray(Player[]::new));
                instance.setPurpleTeam(Arrays.stream(rule.purpleTeam()).map(this::getPlayer).toArray(Player[]::new));
                instance.setTimer(rule.executionTime());
                return instance;
            });
            return;
        }
        if (o instanceof SelectChampRule.Response response) {
            if (!response.ok()) {
                return;
            }
            if (stateManager.getCurrentState() instanceof ChampSelectState state) {
                state.selectChamp(response.rule());
            }
            return;
        }
        if (o instanceof StartGameRule rule) {
            stateManager.getStateQueue().add(() -> {
                InGamestate instance = injector.getInstance(InGamestate.class);
                //todo
                return instance;
            });
        }
        if (o instanceof LoadedIntoGameRule.Response response){
            if (stateManager.getCurrentState() instanceof InGamestate state){
                state.markReady();
            }
        }

    }

    @Override
    public void connected(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void disconnected(Connection connection) {
        if (!exiting) {
            onFailedConnect(JoinRule.Response.Status.SERVER_ERROR);
        }
    }

    public InetAddress scanLAN() {
        return client.discoverHost(App.TCP_PORT, App.UDP_PORT);
    }

    public void onFailedConnect(JoinRule.Response.Status status) {
        logger.warn(status.getError());
        if (stateManager.getCurrentState() instanceof JoinState state) {
            state.error(status.getError());
        } else {
            stateManager.getStateQueue().add(() -> joinStateSupplier(status));
        }
    }

    private JoinState joinStateSupplier(JoinRule.Response.Status status) {
        JoinState state = injector.getInstance(JoinState.class);
        state.error(status.getError());
        return state;
    }

    private PreGameState preGameStateSupplier(JoinTeamRule.Response response) {
        PreGameState state = injector.getInstance(PreGameState.class);
        state.playerJoins(response.rule().teamId(), getPlayer(response.rule().userId()));
        return state;
    }

    public Player getPlayer(String userId) {
        return Arrays.stream(players).filter(p -> p.uuid().equals(userId)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Rule described unknown player!"));
    }

    public void setUsername(String text) {
        this.username = text;
    }

    public void send(Object rule, boolean tcp) {
        if (tcp)
            connection.sendTCP(rule);
        else
            connection.sendUDP(rule);
    }

    public void send(Object rule) {
        send(rule, false);

    }
}
