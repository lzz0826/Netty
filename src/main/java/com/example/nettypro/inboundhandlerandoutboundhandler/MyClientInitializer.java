package com.example.nettypro.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


public class MyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();


        //入站 出站 不會互相影響

        //加入一個出站的handler 對數據進行一個編碼
        pipeline.addLast(new MyLongToByteEncoder());

        //這時一個入站的解碼器(入站handler )
        //pipeline.addLast(new MyByteToLongDecoder());
        pipeline.addLast(new MyByteToLongDecoder2());
        //加入一個自定義的handler ， 處理業務
        pipeline.addLast(new MyClientHandler());


    }
}
