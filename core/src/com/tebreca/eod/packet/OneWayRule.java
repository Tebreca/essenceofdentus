package com.tebreca.eod.packet;

import com.esotericsoftware.kryo.Kryo;

public class OneWayRule extends PacketRule {

    public OneWayRule(Class<?> packet) {
        super(packet, null);
    }

    @Override
    public void register(Kryo kryo) {
        kryo.register(packet());
    }
}
