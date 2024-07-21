package com.example.nettypro.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class TcpChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        //自定解碼器
        pipeline.addLast(new TcpMessageDecoder());

        //自定編碼器
        pipeline.addLast(new TcpMessageEncoder());

        //業務
        pipeline.addLast(new TcpClientChannelHandler());

    }
}
