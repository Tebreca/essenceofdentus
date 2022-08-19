package com.tebreca.eod.packet.rules;

import java.util.Objects;

public final class TeamSyncRule {
    private String[] uuids;
    private short teamid;

    public TeamSyncRule(String[] uuids, short teamid) {
        this.uuids = uuids;
        this.teamid = teamid;
    }

    private TeamSyncRule() {

    }

    public String[] uuids() {
        return uuids;
    }

    public short teamid() {
        return teamid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TeamSyncRule) obj;
        return Objects.equals(this.uuids, that.uuids) &&
                this.teamid == that.teamid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuids, teamid);
    }

    @Override
    public String toString() {
        return "TeamSyncRule[" +
                "uuids=" + uuids + ", " +
                "teamid=" + teamid + ']';
    }


}
