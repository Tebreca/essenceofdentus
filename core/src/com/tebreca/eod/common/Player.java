package com.tebreca.eod.common;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tebreca.eod.client.NeoClient;

import java.util.UUID;

@Singleton
public final class Player {
    private String uuid;
    private String username;

    public Player(UUID uuid, String username) {
        if (uuid == null)
            uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
        this.username = username;
    }

    @Inject
    public Player(NeoClient client) {
        this(UUID.fromString(client.userID), client.username);
    }

    private Player() {
    }

    public String uuid() {
        return uuid;
    }

    public String username() {
        return username;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid.toString();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Player{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
