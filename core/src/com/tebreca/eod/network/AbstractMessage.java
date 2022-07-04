package com.tebreca.eod.network;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.tebreca.eod.helper.IEntry;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public abstract class AbstractMessage implements IEntry, Cloneable {

    public Injector getInjector() {
        return injector;
    }

    private final Injector injector;

    public abstract void read(ByteBuf buf);

    public void write(ByteBuf buf){
        String id = getID();
        buf.writeShort(id.length());
        buf.writeBytes(id.getBytes(StandardCharsets.UTF_8));
        writeMessage(buf);
    }

    protected abstract void writeMessage(ByteBuf buf);


    @Inject
    protected AbstractMessage(Injector injector){
        this.injector = injector;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return newInstance();
    }

    protected abstract AbstractMessage newInstance();
}
