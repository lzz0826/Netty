package com.example.nettypro.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;


public class NettyClientHandler extends ChannelInboundHandlerAdapter {


    /**
     * Creates a client-side handler.
     */
    public NettyClientHandler() {

    }

    //通道就緒觸發
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //將數據寫到緩衝 並 刷新
        //Unpooled.copiedBuffer():Netty 提供的一種方法，用於創建一個非池化的 ByteBuf，並將給定的數據複製到這個緩衝區中。
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,Server", CharsetUtil.UTF_8));
        ctx.flush();
    }




    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        //ByteBuf Netty提供的 不是 NIO 的Bytebuffer
        if (msg instanceof ByteBuf) {
            ByteBuf in = (ByteBuf) msg;
            try {
                if (in.readableBytes() > 0) {
                    String str = in.toString(CharsetUtil.UTF_8);
                    System.out.println("Received Server data: " + str);
                    System.out.println("Client Address: " + ctx.channel().remoteAddress());

                }
            } finally {
                // 釋放資源
                ReferenceCountUtil.release(in);
            }
        }
    }



    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}