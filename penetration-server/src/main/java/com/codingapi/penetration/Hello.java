package com.codingapi.penetration;

import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
@Slf4j
public class Hello implements ClientMessage {

    private String address;

    public static List<Channel> channels = new CopyOnWriteArrayList<>();

    @Override
    public void handle(Channel channel) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.localAddress();
        channels.add(channel);
        log.info("address:{}",inetSocketAddress);
        if(channels.size()==2){
            log.info("send msg  channels:{}",channels);
            Channel peerA =  channels.get(0);
            InetSocketAddress inetSocketAddressA = (InetSocketAddress) peerA.remoteAddress();
            Channel peerB =  channels.get(1);
            InetSocketAddress inetSocketAddressB = (InetSocketAddress) peerB.remoteAddress();
            peerA.writeAndFlush(new ConnectMsg(inetSocketAddressB.getHostString(),inetSocketAddressB.getPort(),inetSocketAddressA.getHostString(),inetSocketAddressA.getPort(),"peerA"));
            peerB.writeAndFlush(new ConnectMsg(inetSocketAddressA.getHostString(),inetSocketAddressA.getPort(),inetSocketAddressB.getHostString(),inetSocketAddressB.getPort(),"peerB"));
            channels.clear();
        }
    }

    public Hello(String address) {
        this.address = address;
    }

    public Hello() {
    }
}
