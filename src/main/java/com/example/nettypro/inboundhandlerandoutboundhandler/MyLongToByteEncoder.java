package com.example.nettypro.inboundhandlerandoutboundhandler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

//如果傳入的消息類型與 MessageToByteEncoder<XXX> 的泛型參數不匹配（例如，傳入的是 String 或其他類型），
//編碼器將不會調用 encode 方法，而是直接將消息傳遞給下一個處理器。
public class MyLongToByteEncoder extends MessageToByteEncoder<Long> {
    //編碼方法
    @Override
    protected void encode(ChannelHandlerContext ctx, Long msg, ByteBuf out) throws Exception {

        System.out.println("MyLongToByteEncoder encode 被調用");
        System.out.println("msg=" + msg);
        out.writeLong(msg);

    }
}
