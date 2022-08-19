package com.tebreca.eod.packet;

import com.esotericsoftware.kryo.Kryo;
import com.tebreca.eod.helper.IEntry;
import com.tebreca.eod.helper.ReflectionHelper;

import java.util.Locale;
import java.util.Objects;

public class PacketRule implements IEntry {
    private final Class<?> packet;
    private final Class<?> response;

    public PacketRule(Class<?> packet, Class<?> response) {
        this.packet = packet;
        this.response = response;
    }

    public void register(Kryo kryo) {
        ReflectionHelper.register(kryo, packet);
        ReflectionHelper.register(kryo, response);
    }

    @Override
    public String getID() {
        return packet.getName().toLowerCase(Locale.ROOT);
    }

    public Class<?> packet() {
        return packet;
    }

    public Class<?> response() {
        return response;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PacketRule) obj;
        return Objects.equals(this.packet, that.packet) &&
                Objects.equals(this.response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packet, response);
    }

    @Override
    public String toString() {
        return "PacketRule[" +
                "packet=" + packet + ", " +
                "response=" + response + ']';
    }


}
