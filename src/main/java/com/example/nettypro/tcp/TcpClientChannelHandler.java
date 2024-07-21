package com.example.nettypro.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


//public class TcpClientChannelHandler extends SimpleChannelInboundHandler<ByteBuf> {
public class TcpClientChannelHandler extends SimpleChannelInboundHandler<TcpMessageProtocol> {

    private int count = 0;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TcpMessageProtocol msg) throws Exception {


        System.out.println("客戶端接收到消息....");

        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("客戶端接收到消息=" + new String(content, StandardCharsets.UTF_8));
        System.out.println("長度=" + len);
        System.out.println("客戶端接收消息數量=" + (++this.count));

        //測試 Tcp粘包拆包實例 因為會有粘包拆包問題所以服務端無法確定訊息的完整性 可能會分段收到
//        byte[] buffer = new byte[msg.readableBytes()];
//        msg.readBytes(buffer);
//
//        String message = new String(buffer, Charset.forName("utf-8"));
//        System.out.println("客戶端接收到消息=" + message);
//        System.out.println("客戶端接收消息數量=" + (++this.count));

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客戶端發消息");

        for(int i = 0 ; i < 5 ; i++){

            String msg = "Client Msg" + i;
            TcpMessageProtocol tcpMessageProtocol = new TcpMessageProtocol();

            byte[] content = msg.getBytes(StandardCharsets.UTF_8);

            tcpMessageProtocol.setLen(msg.getBytes(StandardCharsets.UTF_8).length);
            tcpMessageProtocol.setContent(content);

            ctx.writeAndFlush(tcpMessageProtocol);

        }

        //測試 Tcp粘包拆包實例 因為會有粘包拆包問題所以服務端無法確定訊息的完整性 可能會分段收到
//        for(int i = 0 ; i < 10 ; i++){
//            ByteBuf byteBuf = Unpooled.copiedBuffer("Hello,Server" +i , CharsetUtil.UTF_8);
//            ctx.writeAndFlush(byteBuf);
//        }

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("客戶端異常 :"+cause.getMessage());
        ctx.close();
    }
}
