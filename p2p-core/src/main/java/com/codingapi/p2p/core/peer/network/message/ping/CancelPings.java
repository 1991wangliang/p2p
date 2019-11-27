package com.codingapi.p2p.core.peer.network.message.ping;

import com.codingapi.p2p.core.peer.Peer;
import com.codingapi.p2p.core.peer.network.Connection;
import com.codingapi.p2p.core.peer.network.message.Message;

public class CancelPings implements Message {

    private static final long serialVersionUID = -8650899535821394626L;

    private String peerName;

    public CancelPings(String peerName) {
        this.peerName = peerName;
    }

    @Override
    public void handle(Peer peer, Connection connection) {
        peer.cancelPings(connection, peerName);
    }

    @Override
    public String toString() {
        return "RemovePings{" +
                "peerName='" + peerName + '\'' +
                '}';
    }

}
