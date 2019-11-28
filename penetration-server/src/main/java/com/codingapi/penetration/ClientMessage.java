package com.codingapi.penetration;

import io.netty.channel.Channel;

import java.io.Serializable;

public interface ClientMessage extends Serializable {
    void  handle(Channel channel);
}
