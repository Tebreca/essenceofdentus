package com.tebreca.eod.common.map;

import com.tebreca.eod.common.logic.Hitbox;
import com.tebreca.eod.common.logic.Position;

import java.util.Objects;

public final class Map {
    private Position[] team1spawns;
    private Position[] team2spawns;
    private Hitbox[] hitboxes;

    private String texture;

    private int width;
    private int height;

    public Map(Position[] team1spawns, Position[] team2spawns, Hitbox[] hitboxes, String texture) {
        this.team1spawns = team1spawns;
        this.team2spawns = team2spawns;
        this.hitboxes = hitboxes;
        this.texture = texture;
    }

    public Position[] team1spawns() {
        return team1spawns;
    }

    public Position[] team2spawns() {
        return team2spawns;
    }

    public Hitbox[] hitboxes() {
        return hitboxes;
    }

    public String texture() {
        return texture;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Map) obj;
        return Objects.equals(this.team1spawns, that.team1spawns) &&
                Objects.equals(this.team2spawns, that.team2spawns) &&
                Objects.equals(this.hitboxes, that.hitboxes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(team1spawns, team2spawns, hitboxes);
    }

    @Override
    public String toString() {
        return "Map[" +
                "team1spawns=" + team1spawns + ", " +
                "team2spawns=" + team2spawns + ", " +
                "hitboxes=" + hitboxes + ']';
    }


    public void setTeam1spawns(Position[] team1spawns) {
        this.team1spawns = team1spawns;
    }

    public void setTeam2spawns(Position[] team2spawns) {
        this.team2spawns = team2spawns;
    }

    public void setHitboxes(Hitbox[] hitboxes) {
        this.hitboxes = hitboxes;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
