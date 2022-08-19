package com.tebreca.eod.packet.rules;

@SuppressWarnings("unused")
public class JoinRule {

    String userID;
    String username;

    long timestamp = System.currentTimeMillis();

    public String getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public JoinRule(String userID, String username) {
        this.userID = userID;
        this.username = username;
    }

    private JoinRule() {

    }


    public static class Response {


        public enum Status {
            WELCOME, FULL, INGAME, TIMEOUT, BANNED, SERVER_ERROR;

            public String getError() {
                return switch (this) {
                    case WELCOME -> "";
                    case FULL -> "Server is currently full!";
                    case INGAME -> "Server is already ingame!";
                    case TIMEOUT -> "You've been timed out!";
                    case BANNED -> "You're banned from this server!";
                    case SERVER_ERROR -> "Fatal error in connection!";
                };
            }
        }

        Status status;

        long timeout;

        private Response(Status status) {
            this.status = status;
            this.timeout = 0;
        }

        private Response() {

        }

        private Response(long timeout) {
            this.status = Status.TIMEOUT;
            this.timeout = timeout;
        }

        public Status getStatus() {
            return status;
        }

        public long getTimeout() {
            return timeout;
        }

        public static Response ok() {
            return new Response(Status.WELCOME);
        }

        public static Response ingame() {
            return new Response(Status.INGAME);
        }

        public static Response timeout(long timeout) {
            return new Response(timeout);
        }

        public static Response full() {
            return new Response(Status.FULL);
        }

        public static Response banned() {
            return new Response(Status.BANNED);
        }
    }

}
