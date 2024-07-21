package com.example.nettypro.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

//自定解碼
public class TcpMessageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        System.out.println("TcpMessageDecoder decode 被調用....");

        //需要將得到二進制字節碼-> TcpMessageProtocol 數據包(對象)
        int length = byteBuf.readInt();

        byte[] content = new byte[length];

        byteBuf.readBytes(content);

        //封裝成 MessageProtocol 對象，放入 list， 傳遞下一個handler業務處理
        TcpMessageProtocol tcpMessageProtocol = new TcpMessageProtocol();
        tcpMessageProtocol.setLen(length);
        tcpMessageProtocol.setContent(content);

        //傳遞下一個handler業務處理
        list.add(tcpMessageProtocol);


    }



}
