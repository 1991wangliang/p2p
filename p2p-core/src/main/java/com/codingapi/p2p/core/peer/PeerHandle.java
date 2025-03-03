package com.codingapi.p2p.core.peer;

import com.codingapi.p2p.core.config.P2PConfig;
import com.codingapi.p2p.core.peer.network.Connection;
import com.codingapi.p2p.core.peer.network.PeerChannelHandler;
import com.codingapi.p2p.core.peer.network.PeerChannelInitializer;
import com.codingapi.p2p.core.peer.network.message.Hello;
import com.codingapi.p2p.core.peer.service.ConnectionService;
import com.codingapi.p2p.core.peer.service.IPingService;
import com.codingapi.p2p.core.peer.service.LeadershipService;
import com.codingapi.upnp.UPnP;
import com.google.common.util.concurrent.SettableFuture;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

public class PeerHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeerHandle.class);

    private final Config config;

    private final int portToBind;

    private final EventLoopGroup acceptorEventLoopGroup;

    private final EventLoopGroup networkEventLoopGroup;

    private final EventLoopGroup peerEventLoopGroup;

    private final ObjectEncoder encoder;

    private final Peer peer;

    private Future keepAliveFuture;

    private Future timeoutPingsFuture;

    public PeerHandle(P2PConfig p2PConfig, PeerEventLoopGroup peerEventLoopGroupBean, ConnectionService connectionService, LeadershipService leadershipService, IPingService pingService){
        this.config = new Config(p2PConfig.getPeerName());
        this.portToBind = p2PConfig.getPort();
        acceptorEventLoopGroup = peerEventLoopGroupBean.getAcceptorEventLoopGroup();
        networkEventLoopGroup = peerEventLoopGroupBean.getNetworkEventLoopGroup();
        peerEventLoopGroup = peerEventLoopGroupBean.getPeerEventLoopGroup();
        encoder = peerEventLoopGroupBean.getEncoder();
        this.peer = new Peer(config, connectionService, pingService, leadershipService);
    }


    public String getPeerName() {
        return config.getPeerName();
    }

    public ChannelFuture start() throws InterruptedException {
        //start UPnP
        boolean supportUPnP =  UPnP.isUPnPAvailable();
        if(supportUPnP){
            if(!UPnP.isMappedTCP(portToBind)){
                UPnP.openPortTCP(portToBind);
                LOGGER.info("UPnP openPortTCP:{}",portToBind);
            }
        }

        ChannelFuture closeFuture = null;

        final PeerChannelHandler peerChannelHandler = new PeerChannelHandler(config, peer);
        final PeerChannelInitializer peerChannelInitializer = new PeerChannelInitializer(config, encoder,
                peerEventLoopGroup, peerChannelHandler);
        final ServerBootstrap peerBootstrap = new ServerBootstrap();
        peerBootstrap.group(acceptorEventLoopGroup, networkEventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.SO_BACKLOG, 100)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(peerChannelInitializer);

        final ChannelFuture bindFuture = peerBootstrap.bind(portToBind).sync();

        if (bindFuture.isSuccess()) {
            LOGGER.info("{} Successfully bind to {}", config.getPeerName(), portToBind);
            final Channel serverChannel = bindFuture.channel();

            final SettableFuture<Void> setServerChannelFuture = SettableFuture.create();
            peerEventLoopGroup.execute(() -> {
                try {
                    peer.setBindChannel(serverChannel);
                    setServerChannelFuture.set(null);
                } catch (Exception e) {
                    setServerChannelFuture.setException(e);
                }
            });

            try {
                setServerChannelFuture.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error("Couldn't set bind channel to server " + config.getPeerName(), e);
                System.exit(-1);
            }

            final int initialDelay = Peer.RANDOM.nextInt(config.getKeepAlivePeriodSeconds());

            this.keepAliveFuture = peerEventLoopGroup.scheduleAtFixedRate((Runnable) peer::keepAlivePing, initialDelay, config.getKeepAlivePeriodSeconds(), SECONDS);

            this.timeoutPingsFuture = peerEventLoopGroup.scheduleAtFixedRate((Runnable) peer::timeoutPings, 0, 100, TimeUnit.MILLISECONDS);

            closeFuture = serverChannel.closeFuture();
        } else {
            LOGGER.error(config.getPeerName() + " could not bind to " + portToBind, bindFuture.cause());
            System.exit(-1);
        }

        return closeFuture;
    }

    public CompletableFuture<Collection<String>> ping() {
        final CompletableFuture<Collection<String>> future = new CompletableFuture<>();
        peerEventLoopGroup.execute(() -> peer.ping(future));
        return future;
    }

    public CompletableFuture<Void> leave() {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        peerEventLoopGroup.execute(() -> peer.leave(future));
        if (keepAliveFuture != null && timeoutPingsFuture != null) {
            keepAliveFuture.cancel(false);
            timeoutPingsFuture.cancel(false);
            keepAliveFuture = null;
            timeoutPingsFuture = null;
        }
        //close UPnP
        boolean supportUPnP =  UPnP.isUPnPAvailable();
        if(supportUPnP){
            if(!UPnP.isMappedTCP(portToBind)){
                UPnP.closePortTCP(portToBind);
                LOGGER.info("UPnP closePortTCP:{}",portToBind);
            }
        }
        return future;
    }

    public void scheduleLeaderElection() {
        peerEventLoopGroup.execute(peer::scheduleElection);
    }

    public CompletableFuture<Void> connect(final String host, final int port) {
        final CompletableFuture<Void> connectToHostFuture = new CompletableFuture<>();

        peerEventLoopGroup.execute(() -> peer.connectTo(host, port, connectToHostFuture));

        return connectToHostFuture;
    }

    public void send(String peerName,String message){
        final Hello hello = new Hello();
        hello.setData(message);
        peerEventLoopGroup.execute(()->peer.sendMsg(peerName,hello));
        LOGGER.info("{} Successfully send message {}", config.getPeerName(), message);
    }

    public void disconnect(final String peerName) {
        peerEventLoopGroup.execute(() -> peer.disconnect(peerName));
    }

    public void broadcast(String message) {
        final Hello hello = new Hello();
        hello.setData(message);
        peerEventLoopGroup.execute(()->peer.broadcastMsg(hello));
        LOGGER.info("{} Successfully broadcast message {}", config.getPeerName(), message);
    }

    public Collection<Connection> connections() {
        return peer.connections();
    }
}
