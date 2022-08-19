package com.tebreca.eod.packet.rules;

import java.util.Objects;

public final class StartPreGameRule {

    private long startTime;

    public StartPreGameRule(long startTime) {
        this.startTime = startTime;
    }

    public StartPreGameRule() {
        this(0);
    }

    public long startTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (StartPreGameRule) obj;
        return this.startTime == that.startTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime);
    }

    @Override
    public String toString() {
        return "StartPreGameRule[" +
                "startTime=" + startTime + ']';
    }

}
