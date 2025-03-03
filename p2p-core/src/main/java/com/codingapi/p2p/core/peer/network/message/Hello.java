package com.codingapi.p2p.core.peer.network.message;

import com.codingapi.p2p.core.peer.Peer;
import com.codingapi.p2p.core.peer.network.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lorne
 * @date 2019/11/26
 * @description
 */
public class Hello implements Message {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hello.class);

    private String data;

    @Override
    public void handle(Peer peer, Connection connection) {
        LOGGER.info("read from {} msg, say:'{}'.",connection.getPeerName(),getData());
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
