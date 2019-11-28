package com.codingapi.penetration;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class ConnectMsg implements ClientMessage {

    @Override
    public void handle(final Channel channel) {

    }


    private String host;
    private int port;
    private String name;

    @Override
    public String toString() {
        return "ConnectMsg{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", name='" + name + '\'' +
                '}';
    }
}
