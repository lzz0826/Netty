package com.example.nettypro.dubborpc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;//上下文
    private String result; //返回的結果
    private String para; //客戶端調用方法時，傳入的參數


    //與服務器的連接創建后，就會被調用, 這個方法是第一個被調用(1)
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(" channelActive 被調用  ");
        context = ctx; //因為我們在其它方法會使用到 ctx
    }

    //收到服務器的數據后，調用方法 (4)
    //
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(" channelRead 被調用  ");
        result = msg.toString();
        notify(); //喚醒等待的線程
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    //被代理對象調用, 發送數據給服務器，-> wait -> 等待被喚醒(channelRead) -> 返回結果 (3)-》5
    @Override
    public synchronized Object call() throws Exception {
        System.out.println(" call1 被調用  ");
        context.writeAndFlush(para);
        //進行wait
        wait(); //等待channelRead 方法獲取到服務器的結果后，喚醒
        System.out.println(" call2 被調用  ");
        return  result; //服務方返回的結果

    }
    //(2)
    void setPara(String para) {
        System.out.println(" setPara  ");
        this.para = para;
    }
}
