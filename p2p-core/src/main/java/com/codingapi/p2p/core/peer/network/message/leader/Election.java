package com.codingapi.p2p.core.peer.network.message.leader;

import com.codingapi.p2p.core.peer.Peer;
import com.codingapi.p2p.core.peer.network.Connection;
import com.codingapi.p2p.core.peer.network.message.Message;

/**
 * Notifies other peers about the election started by this peer
 */
public class Election implements Message {

    private static final long serialVersionUID = 3025595002500496571L;

    @Override
    public void handle(Peer peer, Connection connection) {
        peer.handleElection(connection);
    }

}
