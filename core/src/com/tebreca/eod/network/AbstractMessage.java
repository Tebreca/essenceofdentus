package com.tebreca.eod.network;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.tebreca.eod.helper.IEntry;

import java.nio.charset.StandardCharsets;

public abstract class AbstractMessage implements IEntry, Cloneable {

    public Injector getInjector() {
        return injector;
    }

    private final Injector injector;

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
