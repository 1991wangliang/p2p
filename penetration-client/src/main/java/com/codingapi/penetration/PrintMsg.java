package com.codingapi.penetration;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrintMsg implements ClientMessage {

    private String name;

    @Override
    public void handle(Channel channel) {
        log.info("hi:{}",name);
    }
}
