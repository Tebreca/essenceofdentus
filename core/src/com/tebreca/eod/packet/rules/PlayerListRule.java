package com.tebreca.eod.packet.rules;

import com.tebreca.eod.common.Player;

public final class PlayerListRule {
    private final Player[] players;

    public PlayerListRule(Player[] players) {
        this.players = players;
    }

    public PlayerListRule() {
        this(new Player[0]);
    }

    public Player[] players() {
        return players;
    }
}
