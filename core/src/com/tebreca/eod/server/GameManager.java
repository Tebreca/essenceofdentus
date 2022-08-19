package com.tebreca.eod.server;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tebreca.eod.common.Player;
import com.tebreca.eod.common.character.Character;
import com.tebreca.eod.common.character.CharacterRegistry;
import com.tebreca.eod.packet.rules.SelectChampRule;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings({"unchecked", "rawtypes"})
@Singleton
public class GameManager {

    private final Random random = new Random();
    private static final Logger logger = Logger.getLogger(GameManager.class);
    private final CharacterRegistry characterRegistry;

    @Inject
    public GameManager(CharacterRegistry registry) {
        characterRegistry = registry;
    }

    public void setupAppender(Appender appender) {
        logger.addAppender(appender);
    }

    List<Player> playerList = new ArrayList<>();

    public void setPlayerList(List<Player> playerList) {
        this.playerList = playerList;
    }

    List<Player> orangeTeam = new ArrayList<>();
    List<Player> purpleTeam = new ArrayList<>();
    List[] teams = {new ArrayList(), orangeTeam, purpleTeam};
    int maxteamsize = NeoServer.lobbySize / 2;

    Map<Player, Character> playerCharacterMap = new HashMap<>();

    public boolean canfitPlayer(short team) {
        return teams[team].size() + 1 <= maxteamsize;
    }

    public void addToTeam(Player player, short teamId) {
        teams[teamId].add(player);
    }

    public void startGame() {
        //TODO

    }

    private void randomteam(Player player) {
        int orangesize = orangeTeam.size();
        int purplesize = purpleTeam.size();
        boolean orange = orangesize < maxteamsize;
        boolean purple = purplesize < maxteamsize;
        if (orange && purple) {
            addToTeam(player, (short) (random.nextInt(1) + 1));
            return;
        }
        addToTeam(player, (short) (orange ? 1 : 2));
    }

    public boolean hasplayer(String userId) {
        return Stream.concat(purpleTeam.stream(), orangeTeam.stream()).map(Player::uuid)//
                .map(uuid -> uuid.equals(userId)).reduce(Boolean::logicalOr).orElse(false);
    }

    public void handleLeave(Player player) {
        logger.error("Player left midgame!!");
        if (!orangeTeam.remove(player)) {
            purpleTeam.remove(player);
        }
        //TODO
    }

    public void initTeams() {
        int diff = purpleTeam.size() - orangeTeam.size();
        if (purpleTeam.size() + orangeTeam.size() < playerList.size()) {
            playerList.stream().filter(p -> !orangeTeam.contains(p)).filter(p -> !purpleTeam.contains(p)).forEach(this::randomteam);
        } else if (diff > 1) {
            for (int i = 1; i < diff; i += 2) {
                Player player = purpleTeam.get(purpleTeam.size() - 1);
                purpleTeam.remove(player);
                orangeTeam.add(player);
            }
        } else if (diff < -1) {
            for (int i = -1; i > diff; i -= 2) {
                Player player = orangeTeam.get(orangeTeam.size() - 1);
                orangeTeam.remove(player);
                purpleTeam.add(player);
            }
        }
    }

    public String[] getIds(int i) {
        return ((List<Player>) teams[i]).stream().map(Player::uuid).toArray(String[]::new);
    }

    public boolean tryAssignCharacter(SelectChampRule rule) {
        Player player = getFromId(rule.userid());
        Character character = characterRegistry.getEntry(rule.champId());

        if (playerCharacterMap.containsKey(player)) {
            return false;
        }

        Optional<Boolean> flag = playerCharacterMap.entrySet().stream().filter(e -> e.getValue().equals(character))
                .map(e -> orangeTeam.contains(e.getKey()) && orangeTeam.contains(player) || purpleTeam.contains(e.getKey()) && purpleTeam.contains(player))
                .reduce(Boolean::logicalOr);
        if (flag.isPresent() && Boolean.TRUE.equals(flag.get())) {
            return false;
        }
        playerCharacterMap.put(player, character);
        return true;
    }

    Player getFromId(String id) {
        return Stream.concat(orangeTeam.stream(), purpleTeam.stream()).filter(player -> player.uuid().equals(id)).findAny()
                .orElseThrow((() -> new IllegalArgumentException("No player found")));
    }
}
