package com.codingapi.p2p.core.penetration;

import io.netty.channel.Channel;

import java.io.Serializable;

public interface ClientMessage extends Serializable {
    void  handle(Channel channel);
}
