package com.tebreca.eod.common;

import java.util.UUID;

public final class Player {
    private String uuid;
    private String username;

    public Player(UUID uuid, String username) {
        if (uuid == null)
            uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
        this.username = username;
    }

    private Player() {
    }

    public UUID uuid() {
        return UUID.fromString(uuid);
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
}
