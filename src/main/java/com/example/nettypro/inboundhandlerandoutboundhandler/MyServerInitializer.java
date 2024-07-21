package com.example.nettypro.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


public class MyServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();//一會下斷點

        //入站 出站 不會互相影響

        //入站的handler進行解碼 MyByteToLongDecoder
        //pipeline.addLast(new MyByteToLongDecoder());
        pipeline.addLast(new MyByteToLongDecoder2());
        //出站的handler進行編碼
        pipeline.addLast(new MyLongToByteEncoder());
        //自定義的handler 處理業務邏輯
        pipeline.addLast(new MyServerHandler());
        System.out.println("xx");
    }
}
