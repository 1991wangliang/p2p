package com.codingapi.penetration;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Slf4j
public class Hello implements ClientMessage {

    private String address;

    public static List<Channel> channels = new CopyOnWriteArrayList<>();

    @Override
    public void handle(Channel channel) {

    }

    public Hello(String address) {
        this.address = address;
    }

    public Hello() {
    }
}
