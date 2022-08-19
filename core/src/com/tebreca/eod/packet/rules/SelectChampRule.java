package com.tebreca.eod.packet.rules;

import java.util.Objects;

public final class SelectChampRule {
    private String champId;
    private String userId;

    public SelectChampRule(String champId, String userId) {
        this.champId = champId;
        this.userId = userId;
    }

    private SelectChampRule() {
        this("", "");
    }

    public String champId() {
        return champId;
    }

    public String userid() {
        return userId;
    }

    public Response ok() {
        return new Response(this, true);
    }

    public Response negative() {
        return new Response(this, false);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SelectChampRule) obj;
        return Objects.equals(this.champId, that.champId) &&
                Objects.equals(this.userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(champId, userId);
    }

    @Override
    public String toString() {
        return "SelectChampRule[" +
                "champ=" + champId + ", " +
                "userid=" + userId + ']';
    }

    public static final class Response {
        private SelectChampRule rule;
        private boolean ok;

        private Response(SelectChampRule rule, boolean ok) {
            this.rule = rule;
            this.ok = ok;
        }
        private Response(){
            this(null, false);
        }

        public SelectChampRule rule() {
            return rule;
        }

        public boolean ok() {
            return ok;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Response) obj;
            return Objects.equals(this.rule, that.rule) &&
                    this.ok == that.ok;
        }

        @Override
        public int hashCode() {
            return Objects.hash(rule, ok);
        }

        @Override
        public String toString() {
            return "Response[" +
                    "rule=" + rule + ", " +
                    "ok=" + ok + ']';
        }


    }

}
