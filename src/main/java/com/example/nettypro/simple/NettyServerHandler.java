package com.example.nettypro.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;


/**
 *
 * 繼承Netty 規定的某個HandlerAdapter(需要遵守netty規範)
 */
@Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {


    /**
     * 讀取數據實際(讀客戶端的訊息)
     * ChannelHandlerContext(上下文對象):
     * pipeline管道 裡面有很多Handler 業務邏輯處理管道
     * channel 通道 數據的讀和寫 數據通道 可以控制傳給哪個客戶端
     * ...
     *
     * Object msg : 客戶段發送的數據默認 Object
     */

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


        System.out.println("收到客戶端消息");


        //ByteBuf Netty提供的 不是 NIO 的Bytebuffer
        if (msg instanceof ByteBuf) {
            ByteBuf in = (ByteBuf) msg;
            try {
                //模擬超長業務
//                longTimeEvenEventLoop(ctx);
//                longTimeEvenEventLoopSchedule(ctx);

                if (in.readableBytes() > 0) {
                    // 轉換為String
                    String str = in.toString(CharsetUtil.UTF_8);
                    System.out.println("Received Client data: " + str);
                    System.out.println("Client Address: " + ctx.channel().remoteAddress());

                }
            } finally {
                // 釋放資源
                ReferenceCountUtil.release(in);
            }
        }
    }

    /**
     * 數據讀取完畢
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //將數據寫到緩衝 並 刷新
        //Unpooled.copiedBuffer():Netty 提供的一種方法，用於創建一個非池化的 ByteBuf，並將給定的數據複製到這個緩衝區中。
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello,Client",CharsetUtil.UTF_8));
        ctx.flush();
    }


    /**
     * 處理異常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * 模擬超長業務
     * 異步執行 -> 提交該channel 對應的 NIOEventLoop 的 taskQueue中
     * 解決方案1 用戶程序字定義的普通任務 NioEventLoop(單線程)
     *
     * 每個任務都會使用同一個線程 會等上一個ctx.channel().eventLoop().execute()執行完才換下一個
     */
    public void longTimeEvenEventLoop(ChannelHandlerContext ctx) throws InterruptedException {


        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("longTimeEven Compleat",CharsetUtil.UTF_8));
                } catch (Exception e) {
                    System.out.println("發生異常"+e.getMessage());
                }

            }
        });

        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("longTimeEven Compleat22",CharsetUtil.UTF_8));
                } catch (Exception e) {
                    System.out.println("發生異常"+e.getMessage());
                }

            }
        });


    }

    /**
     * 模擬超長業務
     * 解決方案2 Schedule 指定延遲時間任務
     * 會依照延遲時間發送
     */
    public void longTimeEvenEventLoopSchedule(ChannelHandlerContext ctx) throws InterruptedException {

        ctx.channel().eventLoop().schedule(()->{
            try {
                Thread.sleep(10000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("longTimeEven Schedule11",CharsetUtil.UTF_8));
            } catch (Exception e) {
                System.out.println("發生異常"+e.getMessage());
            }
        },4, TimeUnit.SECONDS);

        ctx.channel().eventLoop().schedule(()->{
            try {
                Thread.sleep(10000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("longTimeEven Schedule22",CharsetUtil.UTF_8));
            } catch (Exception e) {
                System.out.println("發生異常"+e.getMessage());
            }
        },2, TimeUnit.SECONDS);

    }



    }
