package com.tebreca.eod.network.messages;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.tebreca.eod.network.AbstractMessage;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

import static com.tebreca.eod.App.injector;

public class ConnectMessage extends AbstractMessage {

    String userId;
    String username;

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public ConnectMessage(Injector injector, String userId, String username) {
        super(injector);
        this.userId = userId;
        this.username = username;
    }

    @Inject
    protected ConnectMessage(Injector injector) {
        super(injector);
    }

    @Override
    public String getID() {
        return "connect";
    }

    @Override
    public void read(ByteBuf buf) {
        short length = buf.readShort();
        userId = buf.toString(buf.readerIndex(), buf.readerIndex() + length, StandardCharsets.UTF_8);
        length = buf.readShort();
        username = buf.toString(buf.readerIndex(), buf.readerIndex() + length, StandardCharsets.UTF_8);
    }

    @Override
    protected void writeMessage(ByteBuf buf) {
        buf.writeShort(userId.length());
        buf.writeBytes(userId.getBytes(StandardCharsets.UTF_8));
        buf.writeShort(username.length());
        buf.writeBytes(username.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected AbstractMessage newInstance() {
        return new ConnectMessage(getInjector());
    }

    public static class Response extends AbstractMessage{

        protected enum Status {
            OK, REJECT, TIMEOUT
        }

        Status status;

        private Response(Injector injector, int timeoutLenght) {
            super(injector);
            status = Status.TIMEOUT;
            this.timeoutLenght = timeoutLenght;
        }

        private Response(Injector injector, Status status) {
            super(injector);
            this.status = status;
        }

        int timeoutLenght=0;

        @Inject
        protected Response(Injector injector) {
            super(injector);
        }

        @Override
        protected AbstractMessage newInstance() {
            return null;
        }

        @Override
        public String getID() {
            return "connect-r";
        }

        @Override
        protected void writeMessage(ByteBuf buf) {
            buf.writeInt(timeoutLenght);
            buf.writeShort(status.ordinal());
        }

        @Override
        public void read(ByteBuf buf) {
            timeoutLenght = buf.readInt();
            status = Status.values()[buf.readShort()];
        }

        public static Response ok(){
            return new Response(injector, Status.OK);
        }

        public static Response reject(){
            return new Response(injector, Status.REJECT);
        }

        public static Response timeout(int timeoutLenght){
            return new Response(injector, timeoutLenght);
        }
    }
}
