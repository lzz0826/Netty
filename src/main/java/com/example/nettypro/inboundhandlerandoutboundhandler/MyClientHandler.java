package com.example.nettypro.inboundhandlerandoutboundhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MyClientHandler  extends SimpleChannelInboundHandler<Long> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Long msg) throws Exception {

        System.out.println("服務器的ip=" + ctx.channel().remoteAddress());
        System.out.println("收到服務器消息=" + msg);

    }

    //重寫channelActive 發送數據

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler 發送數據");
        //ctx.writeAndFlush(Unpooled.copiedBuffer(""))
        ctx.writeAndFlush(123456L); //發送的是一個long

        //分析
        //1. "abcdabcdabcdabcd" 是 16個字節
        //2. 該處理器的前一個handler 是  MyLongToByteEncoder
        //3. MyLongToByteEncoder 父類  MessageToByteEncoder
        //4. 父類  MessageToByteEncoder
        /*

         public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = null;
        try {
            if (acceptOutboundMessage(msg)) { //判斷當前msg 是不是應該處理的類型，如果是就處理，不是就跳過encode
                @SuppressWarnings("unchecked")
                I cast = (I) msg;
                buf = allocateBuffer(ctx, cast, preferDirect);
                try {
                    encode(ctx, cast, buf);
                } finally {
                    ReferenceCountUtil.release(cast);
                }

                if (buf.isReadable()) {
                    ctx.write(buf, promise);
                } else {
                    buf.release();
                    ctx.write(Unpooled.EMPTY_BUFFER, promise);
                }
                buf = null;
            } else {
                ctx.write(msg, promise);
            }
        }
        4. 因此我們編寫 Encoder 是要注意傳入的數據類型和處理的數據類型一致
        */
       // ctx.writeAndFlush(Unpooled.copiedBuffer("abcdabcdabcdabcd",CharsetUtil.UTF_8));

    }
}
