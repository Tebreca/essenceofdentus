package com.tebreca.eod.client;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tebreca.eod.network.AbstractMessage;
import com.tebreca.eod.network.MessageDecoder;
import com.tebreca.eod.network.messages.ConnectMessage;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.impl.SettingsState;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.UUID;

@Singleton
public class NeoClient extends SimpleChannelInboundHandler<AbstractMessage> {

    private final Injector injector;
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    ChannelFuture future;
    Channel channel;
    private String username = "tebreca";
    private String userID = UUID.randomUUID().toString();

    @Inject
    public NeoClient(Injector injector) {
        this.injector = injector;
    }

    public void start() {
        LoggingHandler handler = new LoggingHandler(LogLevel.INFO);
        bootstrap.handler(handler);
        bootstrap.group(workerGroup);
        bootstrap.channel(NioDatagramChannel.class);
        bootstrap.handler(injector.getInstance(MessageDecoder.class));
        bootstrap.handler(this);
    }


    public void connect(String ip, int port){
       future = bootstrap.connect(ip, port);
       channel = future.channel();
       channel.write(new ConnectMessage(injector, userID, username));
    }

    public void disconnect(){
        future.channel().close();
        future.cancel(false);
    }

    public void shutdown(){
        disconnect();
        workerGroup.shutdownGracefully();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
        System.out.println(msg.toString());
        if (msg instanceof ConnectMessage.Response){
            injector.getInstance(GameStateManager.class).setCurrentState(injector.getInstance(SettingsState.class));
        }
    }
}
