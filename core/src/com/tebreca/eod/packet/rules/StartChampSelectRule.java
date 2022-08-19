package com.tebreca.eod.packet.rules;

import java.util.Objects;

public final class StartChampSelectRule {
    private long executionTime;
    private String[] orangeTeam;
    private String[] purpleTeam;

    public StartChampSelectRule(long executionTime, String[] orangeTeam, String[] purpleTeam) {
        this.executionTime = executionTime;
        this.orangeTeam = orangeTeam;
        this.purpleTeam = purpleTeam;
    }

    private StartChampSelectRule(){

    }

    public long executionTime() {
        return executionTime;
    }

    public String[] orangeTeam() {
        return orangeTeam;
    }

    public String[] purpleTeam() {
        return purpleTeam;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StartChampSelectRule) obj;
        return this.executionTime == that.executionTime &&
                Objects.equals(this.orangeTeam, that.orangeTeam) &&
                Objects.equals(this.purpleTeam, that.purpleTeam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executionTime, orangeTeam, purpleTeam);
    }

    @Override
    public String toString() {
        return "StartChampSelectRule[" +
                "executionTime=" + executionTime + ", " +
                "orangeTeam=" + orangeTeam + ", " +
                "purpleTeam=" + purpleTeam + ']';
    }


}
