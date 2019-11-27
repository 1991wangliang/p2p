package com.codingapi.p2p.core.peer.network.message.ping;

import com.codingapi.p2p.core.peer.Peer;
import com.codingapi.p2p.core.peer.network.Connection;
import com.codingapi.p2p.core.peer.network.message.Message;

public class CancelPongs implements Message {

    private static final long serialVersionUID = 5147827390577329607L;

    private final String peerName;

    public CancelPongs(String peerName) {
        this.peerName = peerName;
    }

    @Override
    public void handle(Peer peer, Connection connection) {
        peer.cancelPongs(peerName);
    }

    @Override
    public String toString() {
        return "RemovePongs{" +
                "peerName='" + peerName + '\'' +
                '}';
    }

}
