package com.codingapi.p2p.core.penetration;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {

    private static final EventLoopGroup acceptorEventLoopGroup = new NioEventLoopGroup(1);

    private static final EventLoopGroup networkEventLoopGroup = new NioEventLoopGroup(6);

    public static void main(String[] args) throws InterruptedException {
        final ServerBootstrap peerBootstrap = new ServerBootstrap();
        peerBootstrap.group(acceptorEventLoopGroup, networkEventLoopGroup)
                .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
//                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        final ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new MySimpleHandler());
                    }
                });

        peerBootstrap.bind(8090);

        Thread.sleep(Integer.MAX_VALUE);
    }
}
