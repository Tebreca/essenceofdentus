package com.tebreca.eod.network;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.tebreca.eod.network.MessageRegistry.INSTANCE;

@Singleton
public class MessageDecoder extends MessageToMessageDecoder<DatagramPacket> {

    private final Injector injector;
    @Inject
    public MessageDecoder(Injector injector){
        this.injector = injector;
    }

    /**
     * Decode from one message to an other. This method will be called for each written message that can be handled
     * by this decoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToMessageDecoder} belongs to
     * @param msg the message to decode to an other one
     * @param out the {@link List} to which decoded messages should be added
     * @throws Exception is thrown if an error occurs
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf byteBuf = msg.content();
        short idLength = byteBuf.readShort();
        String messageID = byteBuf.toString(byteBuf.readerIndex(), byteBuf.readerIndex() + idLength, StandardCharsets.UTF_8);
        AbstractMessage message = (AbstractMessage) INSTANCE.getEntry(messageID).clone();
        message.read(byteBuf);
        out.add(message);
    }
}
