package com.example.nettypro.inboundhandlerandoutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyByteToLongDecoder extends ByteToMessageDecoder {
    /**
     *
     * decode 會根據接收的數據，被調用多次, 直到確定沒有新的元素被添加到list
     * , 或者是ByteBuf 沒有更多的可讀字節為止
     * 如果list out 不為空，就會將list的內容傳遞給下一個 channelinboundhandler處理, 該處理器的方法也會被調用多次
     *
     * @param ctx 上下文對象
     * @param in 入站的 ByteBuf
     * @param out List 集合，將解碼后的數據傳給下一個handler
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        System.out.println("MyByteToLongDecoder 被調用");
        //因為 long 8個字節, 需要判斷有8個字節，才能讀取一個long
        if(in.readableBytes() >= 8) {
            out.add(in.readLong());
        }
    }
}
