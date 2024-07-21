package com.example.nettypro.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;



//自定義編碼器
public class TcpMessageEncoder extends MessageToByteEncoder<TcpMessageProtocol> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, TcpMessageProtocol tcpMessageProtocol, ByteBuf byteBuf) throws Exception {
        System.out.println("TcpMessageEncoder encode 被調用....");

        //將需要的 值傳出去 (指定每次 長度 訊息) 避免TCP 拆包 黏包 等導致訊息不完整問題
        byteBuf.writeInt(tcpMessageProtocol.getLen());
        byteBuf.writeBytes(tcpMessageProtocol.getContent());

    }
}
