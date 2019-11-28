package com.codingapi.penetrationclient;

import com.codingapi.penetration.ClientMessage;
import com.codingapi.penetration.PrintMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class MyPeerHandler extends SimpleChannelInboundHandler<ClientMessage> {

    private String name;

    public MyPeerHandler(String name) {
        this.name = name;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ClientMessage message) throws Exception {
        message.handle(channelHandlerContext.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().writeAndFlush(new PrintMsg(name));
    }

}
