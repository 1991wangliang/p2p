package com.codingapi.p2p.core.peer.network.message.leader;

import com.codingapi.p2p.core.peer.Peer;
import com.codingapi.p2p.core.peer.network.Connection;
import com.codingapi.p2p.core.peer.network.message.Message;

/**
 * Notifies the peer ,which started the election, that this peer rejected its election
 */
public class Rejection implements Message {

    private static final long serialVersionUID = -4458007227538796558L;

    @Override
    public void handle(Peer peer, Connection connection) {
        peer.handleRejection(connection);
    }

}
