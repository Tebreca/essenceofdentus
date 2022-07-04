package com.tebreca.eod.network;

import com.google.inject.Singleton;
import com.tebreca.eod.helper.HashRegistry;
import com.tebreca.eod.helper.RegistryHandler;
import com.tebreca.eod.network.messages.ConnectMessage;

import static com.tebreca.eod.App.injector;

@Singleton
public class MessageRegistry extends HashRegistry<AbstractMessage> {

    public static final MessageRegistry INSTANCE = new MessageRegistry();

    static {
        RegistryHandler.subscribe(MessageRegistry::addEntries, AbstractMessage.class);
    }

    private static void addEntries(MessageRegistry registry) {
        registry.register(injector.getInstance(ConnectMessage.class));
        registry.register(injector.getInstance(ConnectMessage.Response.class));
    }

    @Override
    protected AbstractMessage[] createArray(int size) {
        return new AbstractMessage[0];
    }

    @Override
    protected Class<AbstractMessage> getTClass() {
        return AbstractMessage.class;
    }

}
