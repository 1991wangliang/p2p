package com.codingapi.p2p.core.peer.service;

import com.codingapi.p2p.core.peer.Config;
import com.codingapi.p2p.core.peer.network.Connection;
import com.codingapi.p2p.core.peer.network.message.ping.Ping;
import com.codingapi.p2p.core.peer.network.message.ping.Pong;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

/**
 * @author lorne
 * @date 2019/11/27
 * Maintains all of the ongoing Ping operations either initiated by this peer or other peers in the network
 */
public interface IPingService {

    void ping(final CompletableFuture<Collection<String>> future);

    void keepAlive(final boolean discoveryPingEnabled);

    void cancelPings(Connection connection, String removedPeerName);

    void handlePing(InetSocketAddress localAddress, Connection connection, Ping ping);

    void handlePong(Pong pong);

    void cancelPongs(String removedPeerName);

    Collection<Pong> timeoutPings();

    void cancelOwnPing();

    void propagatePingsToNewConnection(Connection connection);
}
