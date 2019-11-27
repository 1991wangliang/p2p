package com.codingapi.p2p.core.penetration;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.sctp.nio.NioSctpServerChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientA {

    public static void main(String[] args) {

        EventLoopGroup networkEventLoopGroup = new NioEventLoopGroup();
        final Bootstrap clientBootstrap = new Bootstrap();
          clientBootstrap.group(networkEventLoopGroup)
                .channel(NioSocketChannel.class)
//                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        final ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new MySimpleHandler());
                    }
                });
//            clientBootstrap.bind(18090);
        clientBootstrap.connect("127.0.0.1",8090);

//        networkEventLoopGroup.execute(() -> {
//            InetSocketAddress socketAddress =(InetSocketAddress)peer.getBindChannel().localAddress();
//            clientBootstrap.bind(socketAddress);
//            log.info("bind address:{}", socketAddress);
//        });


    }
}
