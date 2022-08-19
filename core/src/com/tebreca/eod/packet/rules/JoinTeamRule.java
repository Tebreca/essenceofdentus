package com.tebreca.eod.packet.rules;

import java.util.Objects;

public final class JoinTeamRule {

    private String userId;
    private short teamId;

    public JoinTeamRule(String userId, short teamId) {
        this.userId = userId;
        this.teamId = teamId;
    }

    public JoinTeamRule() {
        this("", (short) 0);
    }

    public Response negative() {
        return new Response(false, this);
    }

    public Response ok() {
        return new Response(true, this);
    }

    public String userId() {
        return userId;
    }

    public short teamId() {
        return teamId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (JoinTeamRule) obj;
        return Objects.equals(this.userId, that.userId) &&
                this.teamId == that.teamId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, teamId);
    }

    @Override
    public String toString() {
        return "JoinTeamRule[" +
                "userId=" + userId + ", " +
                "teamId=" + teamId + ']';
    }


    public static final class Response {

        private boolean ok;
        private JoinTeamRule rule;

        public Response(boolean ok, JoinTeamRule rule) {
            this.ok = ok;
            this.rule = rule;
        }

        public Response() {
            this(false, null);
        }

        public boolean ok() {
            return ok;
        }

        public JoinTeamRule rule() {
            return rule;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Response) obj;
            return this.ok == that.ok &&
                    Objects.equals(this.rule, that.rule);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ok, rule);
        }

        @Override
        public String toString() {
            return "Response[" +
                    "ok=" + ok + ", " +
                    "rule=" + rule + ']';
        }


    }
}
