package com.tebreca.eod.packet;

import com.esotericsoftware.kryo.Kryo;
import com.tebreca.eod.helper.IEntry;
import com.tebreca.eod.helper.ReflectionHelper;

import java.util.Locale;

public record PacketRule(Class<?> packet, Class<?> response) implements IEntry {

    public void register(Kryo kryo) {
        ReflectionHelper.register(kryo, packet);
        ReflectionHelper.register(kryo, response);
    }

    @Override
    public String getID() {
        return packet.getName().toLowerCase(Locale.ROOT);
    }


}
