package com.tebreca.eod.server;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.tebreca.eod.App;
import com.tebreca.eod.network.AbstractMessage;
import com.tebreca.eod.network.MessageDecoder;
import com.tebreca.eod.network.messages.ConnectMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

@Singleton
public class NeoServer extends SimpleChannelInboundHandler<AbstractMessage> implements Runnable {

    private final Injector injector;
    private static final Logger logger = Logger.getLogger(NeoServer.class);
    private NioEventLoopGroup bossGroup;
    private ChannelFuture future;

    @Inject
    public NeoServer(Injector injector) {
        this.injector = injector;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AbstractMessage msg) throws Exception {
        System.out.println(msg.toString());
        /*
            WARN: CURRENTLY NOT AVAILABLE
        switch (msg) {
            case ConnectMessage message:
                ctx.writeAndFlush(ConnectMessage.Response.ok());
                break;
            default:

                break;
        }*/
        if (msg instanceof ConnectMessage message){
            ctx.writeAndFlush(ConnectMessage.Response.ok());
        }
    }


    public boolean start() throws InterruptedException {
        logger.setLevel(Level.INFO);
        if (future != null && !future.isVoid()) {
            logger.error("Server is already running!");
            return false;
        }
        logger.info("Server starting...");
        this.bossGroup = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        LoggingHandler handler = new LoggingHandler(LogLevel.INFO);
        b.group(bossGroup)
                .channel(NioDatagramChannel.class)
                .handler(injector.getInstance(MessageDecoder.class))
                .handler(this)
                .option(ChannelOption.AUTO_CLOSE, true)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(handler);
        future = b.bind(App.DEFAULT_PORT);
        logger.info("Udp transport enabled");
        logger.info("Syncing thread to channelfuture");
        future.sync();
        return true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception thrown in server networking thread:", cause);
        super.exceptionCaught(ctx, cause);
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            start();
        } catch (InterruptedException e) {
            logger.error("Exception thrown in server networking thread:", e);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    public void shutdown() {
        bossGroup.shutdownGracefully();
        try {
            future.channel().close().sync();
        } catch (InterruptedException e) {
            logger.error("Exception thrown while trying to close the netty channel", e);
        } finally {
            future = null;
        }
    }
}
