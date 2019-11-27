package com.codingapi.p2p.core.penetration;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class MySimpleHandler extends SimpleChannelInboundHandler<ClientMessage> {



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ClientMessage message) throws Exception {
        message.handle(channelHandlerContext.channel());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.channel().writeAndFlush(new Hello("hello.."));
    }

}
