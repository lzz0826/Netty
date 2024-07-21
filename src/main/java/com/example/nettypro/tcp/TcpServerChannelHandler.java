package com.example.nettypro.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

//public class TcpServerChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
public class TcpServerChannelHandler extends SimpleChannelInboundHandler<TcpMessageProtocol> {

    private int count = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpMessageProtocol msg) throws Exception {


        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("服務端接收到消息=" + new String(content, StandardCharsets.UTF_8));
        System.out.println("長度=" + len);
        System.out.println("服務端接收消息數量=" + (++this.count));

        //回傳TCP給客戶端

        System.out.println("服務端發送消息...");

        String rep = "Server Msg";
        int length = rep.getBytes(StandardCharsets.UTF_8).length;
        byte[] repContent = rep.getBytes(StandardCharsets.UTF_8);
        TcpMessageProtocol tcpMessageProtocol = new TcpMessageProtocol();
        tcpMessageProtocol.setLen(length);
        tcpMessageProtocol.setContent(repContent);

        ctx.writeAndFlush(tcpMessageProtocol);

//        //測試 Tcp粘包拆包實例 因為會有粘包拆包問題所以服務端無法確定訊息的完整性 可能會分段收到
//        byte[] buffer = new byte[msg.readableBytes()];
//        msg.readBytes(buffer);
//
//        String message = new String(buffer, Charset.forName("utf-8"));
//        System.out.println("服務端端接收到消息=" + message);
//        System.out.println("服務端接收消息數量=" + (++this.count));
//
//        String uuid = UUID.randomUUID().toString();
//
//        ByteBuf byteBuf = Unpooled.copiedBuffer(uuid , CharsetUtil.UTF_8);
//
//        ctx.writeAndFlush(byteBuf);
    }




    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("服務端異常 :"+cause.getMessage());
        ctx.close();
    }
}
