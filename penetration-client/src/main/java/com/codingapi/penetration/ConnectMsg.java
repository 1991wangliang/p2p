package com.codingapi.penetration;

import com.codingapi.penetrationclient.MyPeerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ConnectMsg implements ClientMessage {

    @Override
    public void handle(final Channel channel) {
        log.info("hi:{}",this);
        new Thread(){
            @Override
            public void run() {
                server(channel);
//                channel.close();
                client(channel);
            }
        }.start();
    }


    private void server(Channel channel){
        final EventLoopGroup acceptorEventLoopGroup = new NioEventLoopGroup(1);
        final EventLoopGroup networkEventLoopGroup = new NioEventLoopGroup(6);
        final ServerBootstrap peerBootstrap = new ServerBootstrap();
        peerBootstrap.group(acceptorEventLoopGroup, networkEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        final ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new MyPeerHandler(getName()));
                    }
                });
        peerBootstrap.bind(channel.localAddress());

        log.info("bind {}",channel.localAddress());
    }

    private void client(Channel channel){
        EventLoopGroup networkEventLoopGroup = new NioEventLoopGroup();
        final Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(networkEventLoopGroup)
                .channel(NioSocketChannel.class)
//                 .channel(NioServerSocketChannel.class)
//                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new LoggingHandler(LogLevel.INFO))
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        final ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new MyPeerHandler(getName()));
                    }
                });
        clientBootstrap.connect(getHost(),getPort());
        log.info("connect {}:{}",getHost(),getPort());
    }

    private String host;
    private int port;
    private String name;

    @Override
    public String toString() {
        return "ConnectMsg{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                '}';
    }
}
