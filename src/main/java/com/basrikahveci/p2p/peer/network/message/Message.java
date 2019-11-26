package com.basrikahveci.p2p.peer.network.message;

import com.basrikahveci.p2p.peer.Peer;
import com.basrikahveci.p2p.peer.network.Connection;

import java.io.Serializable;

/**
 * Interfaces of the messages dispatched between peers in the network
 */
public interface Message extends Serializable {

    /**
     * receive message handler, msg is this obj.
     * @param peer self peer
     * @param connection remote connection
     */
    void handle(Peer peer, Connection connection);

}
